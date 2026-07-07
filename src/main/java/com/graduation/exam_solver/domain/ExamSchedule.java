package com.graduation.exam_solver.domain;

import ai.timefold.solver.core.api.domain.solution.PlanningEntityCollectionProperty;
import ai.timefold.solver.core.api.domain.solution.PlanningScore;
import ai.timefold.solver.core.api.domain.solution.PlanningSolution;
import ai.timefold.solver.core.api.domain.solution.ProblemFactCollectionProperty;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider;
import ai.timefold.solver.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;

import java.util.List;


@PlanningSolution
public class ExamSchedule {

    @ValueRangeProvider
    @ProblemFactCollectionProperty
    private List<ExamSlot> examSlots;

   
    @PlanningEntityCollectionProperty
    private List<Exam> exams;

    // النتيجة — Timefold بيحسبها تلقائياً
    // HardMediumSoftScore لأن عندنا 3 مستويات تعارض
    @PlanningScore
    private HardMediumSoftScore score;

    public ExamSchedule() {}

    public ExamSchedule(List<ExamSlot> examSlots, List<Exam> exams) {
        this.examSlots = examSlots;
        this.exams = exams;
    }

    public List<ExamSlot> getExamSlots() { return examSlots; }
    public List<Exam> getExams() { return exams; }
    public HardMediumSoftScore getScore() { return score; }
    public void setScore(HardMediumSoftScore score) { this.score = score; }
}