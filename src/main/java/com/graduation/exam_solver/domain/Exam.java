package com.graduation.exam_solver.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import ai.timefold.solver.core.api.domain.variable.PlanningVariable;

// هاد الكلاس يمثل امتحان واحد
// @PlanningEntity 
@PlanningEntity
public class Exam {

    @PlanningId 

    private String id;
    private String name;
    private int yearOrder;    
    private String priority;  // HIGH, MEDIUM, LOW
    private int avgPreferredDaysBefore;
    private int carryingCount;

   
    @PlanningVariable
    private ExamSlot examSlot;


    public Exam() {}

    public Exam(String id, String name, int yearOrder, String priority,
                int avgPreferredDaysBefore, int carryingCount) {
        this.id = id;
        this.name = name;
        this.yearOrder = yearOrder;
        this.priority = priority;
        this.avgPreferredDaysBefore = avgPreferredDaysBefore;
        this.carryingCount = carryingCount;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getYearOrder() { return yearOrder; }
    public String getPriority() { return priority; }
    public int getAvgPreferredDaysBefore() { return avgPreferredDaysBefore; }
    public int getCarryingCount() { return carryingCount; }
    public ExamSlot getExamSlot() { return examSlot; }
    public void setExamSlot(ExamSlot examSlot) { this.examSlot = examSlot; }

    @Override
    public String toString() { return name; }
}