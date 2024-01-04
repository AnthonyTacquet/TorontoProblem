package be.odisee.logic;

import be.odisee.domain.TimeSlot;
import be.odisee.framework.Solution;

public class Result {

    public static void SolutionPrinter(Solution solution){
        // No exam with id of 0
        int[] result = new int[solution.getExams().size() + 1];
        for(TimeSlot timeSlot : solution.getTimeSlots()){
            timeSlot.getExams().forEach(e -> {
                result[e.getID()] = timeSlot.getID();
            });
        }
        for(int i = 1; i < result.length; i++){
            System.out.println(Result.PrettyPrint(i, result[i]));
        }
    }

    private static String PrettyPrint(int exam, int timeslot){
        if (exam > 9999 || exam < -9999 || timeslot > 9999 || timeslot < -9999)
            return "Id can only be 4 digits long!";

        String examString = "" + exam;
        String timeSlotString = "" + timeslot;

        while (examString.length() < 4){
            examString = " ".concat(examString);
        }

        while (examString.length() < 4){
            examString = " ".concat(examString);
        }

        while (timeSlotString.length() < 4){
            timeSlotString = " ".concat(timeSlotString);
        }

        return examString.concat(timeSlotString);
    }

    public static void SolutionWriter(){
        // Not made yet
    }
}
