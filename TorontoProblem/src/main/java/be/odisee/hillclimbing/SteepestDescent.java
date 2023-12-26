package be.odisee.hillclimbing;

import be.odisee.MyMoveOneRandomExamMove;
import be.odisee.MyObjectiveFunction;
import be.odisee.MySolution;
import be.odisee.MySwapTwoRandomExamsMove;
import be.odisee.data.DataReader;
import be.odisee.framework.Move;
import be.odisee.framework.SearchAlgorithm;
import be.odisee.framework.Solution;

import java.util.Random;

public class SteepestDescent extends SearchAlgorithm {

    private MyObjectiveFunction function;
    private MySolution currentSolution;
    private MySolution bestSolution;
    private double currentResult;
    private double bestResult;

    public SteepestDescent(DataReader dataReader) {
        if (dataReader == null)
            return;
        this.function = new MyObjectiveFunction();
        currentSolution = new MySolution(
                dataReader.getExams().values().stream().toList(),
                dataReader.getTimeslots(),
                dataReader.getStudents().values().stream().toList(),
                true);
        bestSolution = currentSolution;
        bestResult = function.evaluate(bestSolution, null);
    }

    @Override
    public double execute(int numberOfIterations)  {
        currentResult = bestResult;
        Random random = new Random();
        Move move = new MySwapTwoRandomExamsMove();
        Move move2 = new MyMoveOneRandomExamMove();
        int moveId = 0;
        for (int i = 0; i < numberOfIterations; i++) {
            if (currentResult <= bestResult) {
                bestResult = currentResult;
                bestSolution = (MySolution) currentSolution.clone();
                //System.out.println(bestResult);
            }
            else {
                if (moveId == 1)
                    move.undoMove(currentSolution);
                else
                    move2.undoMove(currentSolution);
            }
            // Random between two moves
            moveId = random.nextInt(2) + 1;
            if (moveId == 1)
                currentResult = function.evaluate(currentSolution, move);
            else
                currentResult = function.evaluate(currentSolution, move2);
            if (i % 100 == 0) {
                System.out.println("Iteration #" + i + " " + bestResult + " " + currentResult);
            }
        }
        System.out.println("bestSolution " + function.evaluate(bestSolution, null) + " " + bestResult);
        bestSolution.getTimeSlots().forEach(x-> System.out.println(x+" "));
        return bestResult;
    }

    @Override
    public Solution getBestSolution() {
        return bestSolution;
    }

    @Override
    public Solution getCurrentSolution() {
        return currentSolution;
    }
}
