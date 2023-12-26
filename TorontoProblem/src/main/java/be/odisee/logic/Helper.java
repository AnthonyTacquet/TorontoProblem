package be.odisee.logic;

import be.odisee.domain.Exam;
import be.odisee.domain.TimeSlot;

public class Helper {

    public static boolean IsExamPossibleInTimeSlot(TimeSlot timeSlot, Exam exam){
        // Check if student not in timeslot already
        for (Exam newExam : timeSlot.getExams()) {
            if (newExam == null)
                continue;
            if (newExam.getSID().stream().anyMatch(exam.getSID()::contains)) {
                return false;
            }
        }
        return true;
    }

    public static int DistanceToCost(int distance){
        if (distance > 5)
            return 0;
        else
            return (int) Math.pow(2, 5 - distance);
    }
}
