package com.graduation.exam_solver.domain;

import java.time.LocalDate;

public class ExamSlot {

    private String id;
    private LocalDate date;
    private int slotIndex; 

  
    public ExamSlot() {}

    public ExamSlot(String id, LocalDate date, int slotIndex) {
        this.id = id;
        this.date = date;
        this.slotIndex = slotIndex;
    }

    public String getId() { return id; }
    public LocalDate getDate() { return date; }
    public int getSlotIndex() { return slotIndex; }

    @Override
    public String toString() {
        return date + " Slot" + slotIndex;
    }
}