package com.graduation.exam_solver.constraints;

import ai.timefold.solver.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;
import ai.timefold.solver.core.api.score.stream.Joiners;
import com.graduation.exam_solver.domain.Exam;

public class ExamConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[]{
            hardConflict(factory),
            mediumConflict(factory),
            softConflict(factory),
            preferredDaysGap(factory),
            fixedSubjectConstraint(factory),
            sameGroupConstraint(factory),
            differentGroupsSameYearConstraint(factory)
        };
    }

    private Constraint hardConflict(ConstraintFactory factory) {
        return factory.forEachUniquePair(Exam.class,
                Joiners.equal(exam -> exam.getExamSlot().getDate()))
            .filter((a, b) -> Math.abs(a.getYearOrder() - b.getYearOrder()) <= 1)
            .penalize(HardMediumSoftScore.ONE_HARD)
            .asConstraint("Hard conflict - same day");
    }

    private Constraint mediumConflict(ConstraintFactory factory) {
        return factory.forEachUniquePair(Exam.class,
                Joiners.equal(exam -> exam.getExamSlot().getDate()))
            .filter((a, b) -> Math.abs(a.getYearOrder() - b.getYearOrder()) == 2)
            .penalize(HardMediumSoftScore.ONE_MEDIUM)
            .asConstraint("Medium conflict - same day");
    }

    private Constraint softConflict(ConstraintFactory factory) {
        return factory.forEachUniquePair(Exam.class,
                Joiners.equal(exam -> exam.getExamSlot().getDate()))
            .filter((a, b) -> Math.abs(a.getYearOrder() - b.getYearOrder()) >= 3)
            .penalize(HardMediumSoftScore.ONE_SOFT)
            .asConstraint("Soft conflict - same day");
    }

    private Constraint preferredDaysGap(ConstraintFactory factory) {
        return factory.forEachUniquePair(Exam.class)
            .filter((a, b) -> {
                long daysBetween = Math.abs(
                    a.getExamSlot().getDate().toEpochDay() -
                    b.getExamSlot().getDate().toEpochDay()
                );
                int minDays = Math.max(
                    a.getAvgPreferredDaysBefore(),
                    b.getAvgPreferredDaysBefore()
                );
                return daysBetween < minDays;
            })
            .penalize(HardMediumSoftScore.ONE_SOFT)
            .asConstraint("Preferred days gap not respected");
    }

    

    /**
     * HARD Constraint:
     */
    private Constraint fixedSubjectConstraint(ConstraintFactory factory) {
        return factory.forEach(Exam.class)
            .filter(Exam::isFixed)
            .filter(exam -> {
                if (exam.getExamSlot() == null) return false;
                String slotDate = exam.getExamSlot().getDate().toString();
                int slotIndex = exam.getExamSlot().getSlotIndex();
                return !slotDate.equals(exam.getFixedDate()) ||
                       slotIndex != exam.getFixedTimeslot();
            })
            .penalize(HardMediumSoftScore.ONE_HARD)
            .asConstraint("Fixed subject must be on specified date and timeslot");
    }

    /**
     * HARD Constraint: 
     */
    private Constraint sameGroupConstraint(ConstraintFactory factory) {
        return factory.forEachUniquePair(Exam.class,
                Joiners.equal(Exam::getGroupId))
            .filter((a, b) -> a.getGroupId() != null && !a.getGroupId().isEmpty())
            .filter((a, b) -> {
                if (a.getExamSlot() == null || b.getExamSlot() == null) return false;
                return !a.getExamSlot().getDate().equals(b.getExamSlot().getDate()) ||
                       a.getExamSlot().getSlotIndex() != b.getExamSlot().getSlotIndex();
            })
            .penalize(HardMediumSoftScore.ONE_HARD)
            .asConstraint("Same group subjects must be on same date and timeslot");
    }

     /**
     * HARD Constraint: 
     */
    private Constraint differentGroupsSameYearConstraint(ConstraintFactory factory) {
        return factory.forEachUniquePair(Exam.class,
                Joiners.equal(exam -> exam.getExamSlot().getDate()))
            .filter((a, b) ->
                a.getGroupId() != null && !a.getGroupId().isEmpty() &&
                b.getGroupId() != null && !b.getGroupId().isEmpty() &&
                !a.getGroupId().equals(b.getGroupId()) &&
                a.getYearOrder() == b.getYearOrder()
            )
            .penalize(HardMediumSoftScore.ONE_HARD)
            .asConstraint("Different groups same year cannot share the same day");
    }
}