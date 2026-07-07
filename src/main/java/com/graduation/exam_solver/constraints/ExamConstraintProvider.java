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
            preferredDaysGap(factory) 
        };
    }

    // HARD
    private Constraint hardConflict(ConstraintFactory factory) {
        return factory.forEachUniquePair(Exam.class,
                Joiners.equal(exam -> exam.getExamSlot().getDate()))
            .filter((a, b) -> Math.abs(a.getYearOrder() - b.getYearOrder()) <= 1)
            .penalize(HardMediumSoftScore.ONE_HARD)
            .asConstraint("Hard conflict - same day");
    }

    // MEDIUM
    private Constraint mediumConflict(ConstraintFactory factory) {
        return factory.forEachUniquePair(Exam.class,
                Joiners.equal(exam -> exam.getExamSlot().getDate()))
            .filter((a, b) -> Math.abs(a.getYearOrder() - b.getYearOrder()) == 2)
            .penalize(HardMediumSoftScore.ONE_MEDIUM)
            .asConstraint("Medium conflict - same day");
    }

    // SOFT
    private Constraint softConflict(ConstraintFactory factory) {
        return factory.forEachUniquePair(Exam.class,
                Joiners.equal(exam -> exam.getExamSlot().getDate()))
            .filter((a, b) -> Math.abs(a.getYearOrder() - b.getYearOrder()) >= 3)
            .penalize(HardMediumSoftScore.ONE_SOFT)
            .asConstraint("Soft conflict - same day");
    }

    // SOFT// يفضل ان يكون بين كل امتحان و امتحان اخر مسافة ايام معينة
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
}