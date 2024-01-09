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

public class MyMoveOneRandomExamMove extends Move {
    private int examId;
    private int timeSlotIdOrigin;
    private int timeSlotIdDestination;
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
        findExam();
        // Calculate distance before item switch
        double costBefore = calculateCost();
        // Swap two items in list
        moveExam(timeSlots, timeSlotIdOrigin, timeSlotIdDestination, examId);
        // calculate distance after swap
        double costAfter = calculateCost();

        double newCost = ((originalCost * students.size()) + costAfter - costBefore) / students.size();
        delta = newCost - originalCost;
        solution.setObjectiveValue(newCost);
        return newCost;
    }
    // Same as absoluteEvaluation
    private double calculateCost(){
        int cost = 0;
        Set<Integer> studentsToCalculate = new HashSet<>();
        studentsToCalculate.addAll(exams.stream().filter(e -> e.getID() == examId).findFirst().get().getSID()); // Add all students from first exam

        // Loop through each timeslot
        // Stop looping one before the end, last timeslot can't calculate a cost
        for (int i = 0; i < timeSlots.size() - 1; i++){
            TimeSlot timeSlot = timeSlots.get(i);
            // Loop through each student in exam
            for (int studentId : timeSlot.getAllSIDInTimeSlot().stream().filter(e -> studentsToCalculate.contains(e)).toList()){
                // Loop through the following timeslots
                for (int j = i + 1; j < timeSlots.size(); j++){
                    // If timeslot has the same student as the timeslot above, calculate cost
                    if (timeSlots.get(j).getAllSIDInTimeSlot().stream().filter(e -> studentsToCalculate.contains(e)).anyMatch(e -> e == studentId))
                        cost += Helper.DistanceToCost(j - i);
                }
            }
        }
        return cost;
    }

    private void findExam(){
        // SwapExams
        Random random = new Random();
        boolean again;

        // First and second position cannot be the same
        do {
            again = false;
            this.examId = exams.get(random.nextInt(exams.size() - 1)).getID();

            List<TimeSlot> tempTimeSlots = new ArrayList<>();
            for (TimeSlot originalTimeSlot : timeSlots) {
                tempTimeSlots.add(new TimeSlot(originalTimeSlot));
            }
            // Get timeslot from exam
            Optional<TimeSlot> timeSlotExamOneOptional =  tempTimeSlots.stream().filter(e -> e.getExams().stream().anyMatch(x -> x.getID() == examId)).findFirst();

            // If exam not found throw
            // Debug: timeSlots.stream().flatMap(e -> e.getExams().stream()).count()
            if (timeSlotExamOneOptional.isEmpty())
                throw new RuntimeException("Exam not found when trying to swap");

            TimeSlot timeSlotExamOrigin = timeSlotExamOneOptional.get();

            // Remove exam from temp list
            timeSlotExamOrigin.setExams(timeSlotExamOrigin.getExams().stream().filter(e -> e.getID() != examId).toList());

            // If exam breaks hard constraint, not valid
            try{
                // Check if origin exam fits in random timeslot
                timeSlotIdDestination = random.nextInt(timeSlots.size());
                if (!Helper.IsExamPossibleInTimeSlot(timeSlots.get(timeSlotIdDestination), exams.stream().filter(e -> e.getID() == examId).findFirst().get()))
                    again = true;
            } catch (Exception exception){
                throw new RuntimeException("Exam not found in exam list");
            }

            timeSlotIdOrigin = timeSlotExamOrigin.getID();

        } while(again);
    }

    private void moveExam(List<TimeSlot> timeSlots, int timeSlotIdOrigin, int timeSlotIdDestination, int examId){
        try{
            // Get timeslots
            TimeSlot timeSlotOrigin = timeSlots.stream().filter(e -> e.getID() == timeSlotIdOrigin).findFirst().get();
            TimeSlot timeSlotDestination = timeSlots.stream().filter(e -> e.getID() == timeSlotIdDestination).findFirst().get();

            // Get exams
            Exam exam = exams.stream().filter(e -> e.getID() == examId).findFirst().get();

            // Remove the exam from origin
            timeSlotOrigin.setExams(timeSlotOrigin.getExams().stream().filter(e -> e.getID() != examId).toList());

            // Add the exam to destination
            timeSlotDestination.setExams(Stream.concat(timeSlotDestination.getExams().stream(), Stream.of(exam)).collect(Collectors.toList()));

        } catch (Exception exception){
            throw new RuntimeException("Problem occurred when swapping the two exams from timeslot");
        }
    }

    @Override
    public void undoMove(Solution solution) {
        List<TimeSlot> timeSlotRevert = solution.getTimeSlots();
        moveExam(timeSlotRevert, timeSlotIdDestination, timeSlotIdOrigin, examId);

        solution.setObjectiveValue(solution.getObjectiveValue() - this.delta);
        solution.setTimeSlots(timeSlotRevert);
    }
}

