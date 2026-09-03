package com.graduation.exam_solver.rest;

import ai.timefold.solver.core.api.solver.SolverManager;
import com.graduation.exam_solver.domain.Exam;
import com.graduation.exam_solver.domain.ExamSchedule;
import com.graduation.exam_solver.domain.ExamSlot;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/solve")
public class ExamSchedulerController {

    private final SolverManager<ExamSchedule, UUID> solverManager;

    public ExamSchedulerController(SolverManager<ExamSchedule, UUID> solverManager) {
        this.solverManager = solverManager;
    }

    @PostMapping
    public ExamSchedule solve(@RequestBody Map<String, Object> payload) throws Exception {

        // 1. examPeriod
        Map<String, Object> period = (Map<String, Object>) payload.get("examPeriod");
        LocalDate startDate = LocalDate.parse((String) period.get("startDate").toString().substring(0, 10));
        LocalDate endDate = LocalDate.parse((String) period.get("endDate").toString().substring(0, 10));
        int timeslotsPerDay = (int) period.get("timeslotsPerDay");

        List<String> excludedDatesRaw = (List<String>) period.getOrDefault("excludedDates", List.of());
        List<Integer> excludedDays = (List<Integer>) period.getOrDefault("excludedDaysOfWeek", List.of(5, 6));

        Set<LocalDate> excludedDates = new HashSet<>();
        for (String d : excludedDatesRaw) {
            excludedDates.add(LocalDate.parse(d.substring(0, 10)));
        }

    
        List<ExamSlot> slots = new ArrayList<>();
        LocalDate current = startDate;
        int slotId = 1;

        while (!current.isAfter(endDate)) {
            if (!excludedDays.contains(current.getDayOfWeek().getValue() % 7) &&
                !excludedDates.contains(current)) {
                for (int s = 1; s <= timeslotsPerDay; s++) {
                    slots.add(new ExamSlot("slot-" + slotId + "-" + s, current, s));
                }
            }
            current = current.plusDays(1);
            slotId++;
        }

    
        List<Map<String, Object>> examsRaw = (List<Map<String, Object>>) payload.get("exams");
        List<Exam> exams = new ArrayList<>();

        for (Map<String, Object> e : examsRaw) {
            String groupId = (String) e.getOrDefault("groupId", null);
            Boolean isFixed = (Boolean) e.getOrDefault("isFixed", false);
            String fixedDate = (String) e.getOrDefault("fixedDate", null);
            Integer fixedTimeslot = e.get("fixedTimeslot") != null ?
                ((Number) e.get("fixedTimeslot")).intValue() : null;

            exams.add(new Exam(
                (String) e.get("id"),
                (String) e.get("name"),
                (int) e.get("yearOrder"),
                (String) e.get("priority"),
                (int) e.getOrDefault("avgPreferredDaysBefore", 3),
                (int) e.getOrDefault("carryingCount", 0),
                groupId,
                isFixed != null && isFixed,
                fixedDate,
                fixedTimeslot
            ));
        }
    
        Map<String, ExamSlot> slotLookup = new HashMap<>();
        for (ExamSlot slot : slots) {
            String key = slot.getDate().toString() + "-" + slot.getSlotIndex();
            slotLookup.put(key, slot);
        }

        for (Exam exam : exams) {
            if (exam.isFixed() && exam.getFixedDate() != null && exam.getFixedTimeslot() != null) {
                String key = exam.getFixedDate() + "-" + exam.getFixedTimeslot();
                ExamSlot matchedSlot = slotLookup.get(key);
                if (matchedSlot != null) {
                    exam.setExamSlot(matchedSlot);
                    exam.setPinned(true);
                }
            }
        }

        Map<String, List<Exam>> examsByGroup = new HashMap<>();
        for (Exam exam : exams) {
            if (exam.getGroupId() != null && !exam.getGroupId().isEmpty()) {
                examsByGroup.computeIfAbsent(exam.getGroupId(), k -> new ArrayList<>()).add(exam);
            }
        }

        int slotCursor = 0; 

        for (Map.Entry<String, List<Exam>> entry : examsByGroup.entrySet()) {
            List<Exam> groupExams = entry.getValue();

            
            ExamSlot chosenSlot = null;
            for (Exam e : groupExams) {
                if (e.isPinned() && e.getExamSlot() != null) {
                    chosenSlot = e.getExamSlot();
                    break;
                }
            }

        
            if (chosenSlot == null && !slots.isEmpty()) {
                chosenSlot = slots.get(slotCursor % slots.size());
                slotCursor++;
            }

            if (chosenSlot != null) {
                for (Exam e : groupExams) {
                    e.setExamSlot(chosenSlot);
                    e.setPinned(true);
                }
            }
        }

    
        ExamSchedule problem = new ExamSchedule(slots, exams);
        UUID jobId = UUID.randomUUID();

        ExamSchedule solution = solverManager.solve(jobId, problem).getFinalBestSolution();

        return solution;
    }
}