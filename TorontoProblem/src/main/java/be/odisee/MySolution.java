package be.odisee;

import be.odisee.domain.Exam;
import be.odisee.domain.Student;
import be.odisee.domain.TimeSlot;
import be.odisee.framework.Solution;
import be.odisee.logic.Helper;

import java.util.*;

public class MySolution implements Solution {

    private List<TimeSlot> timeSlots;
    private List<Exam> exams;
    private List<Student> students;
    private double objectiveValue = 0;

    public MySolution(List<Exam> exams, List<TimeSlot> timeSlots, List<Student> students, boolean fill) {
        this.timeSlots = timeSlots;
        this.students = students;
        this.exams = exams;
        if (fill)
            fillTimeSlots();
    }

    public MySolution(List<Exam> exams, List<TimeSlot> timeSlots, List<Student> students) {
        this.timeSlots = timeSlots;
        this.students = students;
        this.exams = exams;
    }
    // Fill the exams list in timeSlots with all the data, not yet optimal
    // Fill the first 10 exams, then order exams by amount how many timeslots they fit
    private void fillTimeSlots() {
        List<Exam> tempExams = new ArrayList<>();
        tempExams.addAll(exams);

        do {
            AddFirstFewExamsBasedOnScore(tempExams);
        } while (tempExams.size() > 0);
    }

    private void AddFirstFewExamsBasedOnScore(List<Exam> exams){
        int iteration = 5;
        LinkedHashMap<Exam, Integer> scoreForExams = ScoreForExam(exams);
        List<Exam> listScoreExams = scoreForExams.keySet().stream().toList();

        // Set first x exams in the timeslot, the smaller the iteration the preciser
        for (int i = 0; i < listScoreExams.size(); i++){
            Exam exam = listScoreExams.get(i);

            if (i >= iteration)
                break;

            boolean found = false;
            for (TimeSlot timeSlot : timeSlots) {
                if (Helper.IsExamPossibleInTimeSlot(timeSlot, exam)) {
                    timeSlot.addExam(exam);
                    exams.remove(exam);
                    found = true;
                    break;
                }
            }
            if (!found)
                throw new RuntimeException("No exam timeslot found, try again");
        }
    }

    private LinkedHashMap<Exam, Integer> ScoreForExam(List<Exam> exams){
        HashMap<Exam, Integer> scoreForExams = new HashMap<>();
        for (Exam exam : exams){
            int score = 0;
            for (TimeSlot timeSlot : timeSlots) {
                if (Helper.IsExamPossibleInTimeSlot(timeSlot, exam))
                    score++;
            }
            scoreForExams.put(exam, score);
        }
        LinkedHashMap<Exam, Integer> sortedMap = new LinkedHashMap<>();
        scoreForExams.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEachOrdered(entry -> sortedMap.put(entry.getKey(), entry.getValue()));
        return sortedMap;
    }

    @Override
    public double getObjectiveValue() {
        return this.objectiveValue;
    }

    @Override
    public void setObjectiveValue(double value) {
        this.objectiveValue = value;
    }

    @Override
    public Object clone() {
        MySolution mySolution = new MySolution(exams, timeSlots, students);
        List<TimeSlot> newTimeSlot = new ArrayList<>();
        newTimeSlot.addAll(timeSlots);

        mySolution.setTimeSlots(newTimeSlot);
        mySolution.setObjectiveValue(this.getObjectiveValue());
        return mySolution;
    }

    public List<TimeSlot> getTimeSlots() {
        return timeSlots;
    }
    public List<Exam> getExams(){
        return exams;
    }
    public List<Student> getStudents(){
        return students;
    }
    public void setTimeSlots(List<TimeSlot> newTimeSlot) {
        this.timeSlots = newTimeSlot;
    }
    public void setExams(List<Exam> exams){
        this.exams = exams;
    }
    public void setStudents(List<Student> students){
        this.students = students;
    }
}
