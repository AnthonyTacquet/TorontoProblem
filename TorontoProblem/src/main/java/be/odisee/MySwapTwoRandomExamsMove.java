package be.odisee;

import be.odisee.domain.Exam;
import be.odisee.domain.Student;
import be.odisee.domain.TimeSlot;
import be.odisee.framework.Move;
import be.odisee.framework.Solution;
import be.odisee.logic.Helper;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MySwapTwoRandomExamsMove extends Move {
    private int examIdOne;
    private int examIdTwo;
    private int timeSlotIdOne;
    private int timeSlotIdTwo;
    private double delta;
    private List<TimeSlot> timeSlots;
    private List<Exam> exams;
    private List<Student> students;
    @Override
    public double doMove(Solution solution) {
        this.timeSlots = solution.getTimeSlots();
        this.exams = solution.getExams();
        this.students = solution.getStudents();

        if (timeSlots == null || timeSlots.isEmpty() || exams == null || exams.isEmpty())
            return 0;

        double originalCost = solution.getObjectiveValue();
        // Find exams
        findExams();
        // Calculate distance before item switch
        double costBefore = calculateCost();
        // Swap two items in list
        swapExams(timeSlots, timeSlotIdOne, timeSlotIdTwo, examIdOne, examIdTwo);
        // calculate distance after swap
        double costAfter = calculateCost();

        double newCost = ((originalCost * students.size()) + costAfter - costBefore) / students.size();
        delta = newCost - originalCost;
        solution.setObjectiveValue(newCost);
        return newCost;
    }

    // Way faster than absolute evaluation
    private double calculateCost(){
        int cost = 0;
        Set<Integer> studentsToCalculate = new HashSet<>();
        studentsToCalculate.addAll(exams.stream().filter(e -> e.getID() == examIdOne).findFirst().get().getSID()); // Add all students from first exam
        studentsToCalculate.addAll(exams.stream().filter(e -> e.getID() == examIdTwo).findFirst().get().getSID()); // Add all students from second exam

        // Loop through each timeslot
        // Stop looping one before the end, last timeslot can't calculate a cost
        for (int i = 0; i < timeSlots.size() - 1; i++){
            TimeSlot timeSlot = timeSlots.get(i);
            // Loop through each student in exam
            for (int studentId : timeSlot.getAllSIDInTimeSlot().stream().filter(e -> studentsToCalculate.contains(e)).toList()){
                // Loop through the following timeslots, timeslots after 'timeslot' variable
                for (int j = i + 1; j < timeSlots.size(); j++){
                    // If timeslot has the same student as the timeslot above, calculate cost
                    if (timeSlots.get(j).getAllSIDInTimeSlot().stream().filter(e -> studentsToCalculate.contains(e)).anyMatch(e -> e == studentId))
                        cost += Helper.DistanceToCost(j - i);
                }
            }
        }
        return cost;
    }

    private void findExams(){
        // SwapExams
        Random random = new Random();
        boolean again;

        // First and second position cannot be the same
        do {
            again = false;
            this.examIdOne = exams.get(random.nextInt(exams.size() - 1)).getID();
            this.examIdTwo = exams.get(random.nextInt(exams.size() - 1)).getID();

            List<TimeSlot> tempTimeSlots = new ArrayList<>();
            for (TimeSlot originalTimeSlot : timeSlots) {
                tempTimeSlots.add(new TimeSlot(originalTimeSlot));
            }
            // Get timeslot from exam
            Optional<TimeSlot> timeSlotExamOneOptional =  tempTimeSlots.stream().filter(e -> e.getExams().stream().anyMatch(x -> x.getID() == examIdOne)).findFirst();
            Optional<TimeSlot>  timeSlotExamTwoOptional =  tempTimeSlots.stream().filter(e -> e.getExams().stream().anyMatch(x -> x.getID() == examIdTwo)).findFirst();

            // If exam not found throw
            // Debug: timeSlots.stream().flatMap(e -> e.getExams().stream()).count()
            if (timeSlotExamOneOptional.isEmpty() || timeSlotExamTwoOptional.isEmpty())
                throw new RuntimeException("Exam not found when trying to swap");

            TimeSlot timeSlotExamOne = timeSlotExamOneOptional.get();
            TimeSlot timeSlotExamTwo = timeSlotExamTwoOptional.get();

            // If exams in same timeslot, is not valid
            if (timeSlotExamOne.getID() == timeSlotExamTwo.getID())
                again = true;

            // Remove both exams from temp list
            timeSlotExamOne.setExams(timeSlotExamOne.getExams().stream().filter(e -> e.getID() != examIdOne).toList());
            timeSlotExamTwo.setExams(timeSlotExamTwo.getExams().stream().filter(e -> e.getID() != examIdTwo).toList());

            // If exam breaks hard constraint, not valid
            try{
                // Check if second exam fits in timeslot of first exam
                if (!Helper.IsExamPossibleInTimeSlot(timeSlotExamOne, exams.stream().filter(e -> e.getID() == examIdTwo).findFirst().get()))
                    again = true;
                // Check if first exam fits in timeslot of second exam
                if (!Helper.IsExamPossibleInTimeSlot(timeSlotExamTwo, exams.stream().filter(e -> e.getID() == examIdOne).findFirst().get()))
                    again = true;
            } catch (Exception exception){
                throw new RuntimeException("Exam not found in exam list");
            }
            // The exams can't be the same
            if (examIdOne == examIdTwo)
                again = true;

            timeSlotIdOne = timeSlotExamOne.getID();
            timeSlotIdTwo = timeSlotExamTwo.getID();

        } while(again);
    }

    private void swapExams(List<TimeSlot> timeSlots, int timeSlotIdOne, int timeSlotIdTwo, int examIdOne, int examIdTwo){
        try{
            // Get timeslots
            TimeSlot timeSlotOne = timeSlots.stream().filter(e -> e.getID() == timeSlotIdOne).findFirst().get();
            TimeSlot timeSlotTwo = timeSlots.stream().filter(e -> e.getID() == timeSlotIdTwo).findFirst().get();

            // Get exams
            Exam examOne = exams.stream().filter(e -> e.getID() == examIdOne).findFirst().get();
            Exam examTwo = exams.stream().filter(e -> e.getID() == examIdTwo).findFirst().get();

            // Remove the exams
            timeSlotOne.setExams(timeSlotOne.getExams().stream().filter(e -> e.getID() != examIdOne).toList());
            timeSlotTwo.setExams(timeSlotTwo.getExams().stream().filter(e -> e.getID() != examIdTwo).toList());

            // Add the exams
            timeSlotOne.setExams(Stream.concat(timeSlotOne.getExams().stream(), Stream.of(examTwo)).collect(Collectors.toList()));
            timeSlotTwo.setExams(Stream.concat(timeSlotTwo.getExams().stream(), Stream.of(examOne)).collect(Collectors.toList()));

        } catch (Exception exception){
            throw new RuntimeException("Problem occurred when swapping the two exams from timeslot");
        }
    }

    @Override
    public void undoMove(Solution solution) {
        List<TimeSlot> timeSlotRevert = solution.getTimeSlots();
        swapExams(timeSlotRevert, timeSlotIdOne, timeSlotIdTwo, examIdTwo, examIdOne);

        solution.setObjectiveValue(solution.getObjectiveValue() - delta);
        solution.setTimeSlots(timeSlotRevert);
    }
}
