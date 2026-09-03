package com.graduation.exam_solver.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.entity.PlanningPin; 
import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import ai.timefold.solver.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public class Exam {

    @PlanningId
    private String id;
    private String name;
    private int yearOrder;
    private String priority;
    private int avgPreferredDaysBefore;
    private int carryingCount;

    private String groupId;
    private boolean isFixed;
    private String fixedDate;
    private Integer fixedTimeslot;

    private boolean pinned;

    @PlanningVariable
    private ExamSlot examSlot;

    public Exam() {}

    public Exam(String id, String name, int yearOrder, String priority,
                int avgPreferredDaysBefore, int carryingCount,
                String groupId, boolean isFixed, String fixedDate, Integer fixedTimeslot) {
        this.id = id;
        this.name = name;
        this.yearOrder = yearOrder;
        this.priority = priority;
        this.avgPreferredDaysBefore = avgPreferredDaysBefore;
        this.carryingCount = carryingCount;
        this.groupId = groupId;
        this.isFixed = isFixed;
        this.fixedDate = fixedDate;
        this.fixedTimeslot = fixedTimeslot;
        this.pinned = false;
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public int getYearOrder() { return yearOrder; }
    public String getPriority() { return priority; }
    public int getAvgPreferredDaysBefore() { return avgPreferredDaysBefore; }
    public int getCarryingCount() { return carryingCount; }
    public String getGroupId() { return groupId; }
    public boolean isFixed() { return isFixed; }
    public String getFixedDate() { return fixedDate; }
    public Integer getFixedTimeslot() { return fixedTimeslot; }
    public ExamSlot getExamSlot() { return examSlot; }
    public void setExamSlot(ExamSlot examSlot) { this.examSlot = examSlot; }

    @PlanningPin
    public boolean isPinned() { return pinned; }
    public void setPinned(boolean pinned) { this.pinned = pinned; }

    @Override
    public String toString() { return name; }
}