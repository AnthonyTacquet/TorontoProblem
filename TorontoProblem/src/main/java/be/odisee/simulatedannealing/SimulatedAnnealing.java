package be.odisee.simulatedannealing;

import be.odisee.MyMoveOneRandomExamMove;
import be.odisee.MyObjectiveFunction;
import be.odisee.MySolution;
import be.odisee.MySwapTwoRandomExamsMove;
import be.odisee.data.DataReader;
import be.odisee.framework.*;

import java.util.ArrayList;
import java.util.Random;


public class SimulatedAnnealing extends SearchAlgorithm {
    private ObjectiveFunction function;
    private Solution currentSolution;
    private Solution bestSolution;
    private double currentResult;
    private double bestResult;

    public SimulatedAnnealing(DataReader dataReader) {
        if (dataReader == null)
            return;
        this.function = new MyObjectiveFunction();
        currentSolution = new MySolution(
                dataReader.getExams().values().stream().toList(),
                dataReader.getTimeslots(),
                dataReader.getStudents().values().stream().toList(),
                true);
        bestSolution = currentSolution;
        bestResult = function.evaluate(bestSolution,null);
    }

    @Override
    public double execute(int numberOfIterations) {
        currentResult = bestResult;
        Random random = new Random();
        Move move = new MySwapTwoRandomExamsMove();
        Move move2 = new MyMoveOneRandomExamMove();
        int moveId = 0;
        for (int i = 0; i <= numberOfIterations; i++) {
            if (currentResult < bestResult) {
                bestResult = currentResult;
                bestSolution = (MySolution)currentSolution.clone();
            } else if (Math.exp((bestResult - currentResult) / Math.sqrt(currentResult) / (1.0001 - ((i * 1.0) / numberOfIterations))) < RandomGenerator.random.nextDouble()) {
                if (moveId == 1)
                    currentResult = function.evaluate(currentSolution, move);
                else
                    currentResult = function.evaluate(currentSolution, move2);
            }
            moveId = random.nextInt(2) + 1;
            if (moveId == 1)
                currentResult = function.evaluate(currentSolution, move);
            else
                currentResult = function.evaluate(currentSolution, move2);

            if (i % 100 == 0) { // Was 10000
                System.out.println("Iteration #" + i + " " + bestResult + " " + currentResult + " " + (1.0001 - ((1.0 * i) / numberOfIterations)));
            }
        }
        System.out.println("bestSolution " + function.evaluate(bestSolution,null) + " " + bestResult);
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
