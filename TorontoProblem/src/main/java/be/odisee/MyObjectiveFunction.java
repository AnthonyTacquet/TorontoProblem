package be.odisee;

import be.odisee.domain.TimeSlot;
import be.odisee.framework.Move;
import be.odisee.framework.ObjectiveFunction;
import be.odisee.framework.Solution;
import be.odisee.logic.Helper;

import java.util.List;

public class MyObjectiveFunction extends ObjectiveFunction {
    @Override
    public double evaluate(Solution solution, Move move) {
        if (move == null){
            double result = absoluteEvaluation(solution);
            solution.setObjectiveValue(result);
            return result;
        } else {
            return deltaEvaluation(solution, move);
        }
    }

    private double absoluteEvaluation(Solution solution){
        List<TimeSlot> timeSlots = solution.getTimeSlots();
        int cost = 0;
        // Loop through each timeslot
        // Stop looping one before the end, last timeslot can't calculate a cost
        for (int i = 0; i < timeSlots.size() - 1; i++){
            TimeSlot timeSlot = timeSlots.get(i);
            // Loop through each student in this timeslot
            for (int studentId : timeSlot.getAllSIDInTimeSlot()){
                // Loop through the following timeslots
                for (int j = i + 1; j < timeSlots.size(); j++){
                    // If timeslot has the same student as the timeslot above, calculate cost
                    if (timeSlots.get(j).getAllSIDInTimeSlot().stream().anyMatch(e -> e == studentId))
                        cost += Helper.DistanceToCost(j - i);
                }
            }
        }
        return ((double) cost) / solution.getStudents().size();
    }
    private double deltaEvaluation(Solution solution, Move move) {
        return move.doMove(solution);
    }



    // Calculate cost
    /*
    * ○ 16 als een student 2 aaneensluitende examens heeft
    * ○ 8 als er 1 tijdslot tussen 2 opeenvolgende examens zit
    * ○ 4 als er 2 tijdsloten tussen 2 opeenvolgende examens zit
    * ○ 2 als er 3 tijdsloten tussen 2 opeenvolgende examens zit
    * ○ 1 als er 4 tijdsloten tussen 2 opeenvolgende examens zit
    */

}
