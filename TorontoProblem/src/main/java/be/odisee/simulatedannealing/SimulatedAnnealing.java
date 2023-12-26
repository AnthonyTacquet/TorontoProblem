package be.odisee.simulatedannealing;

import be.odisee.MyObjectiveFunction;
import be.odisee.MySolution;
import be.odisee.MySwapTwoRandomExamsMove;
import be.odisee.framework.*;

import java.util.ArrayList;


public class SimulatedAnnealing extends SearchAlgorithm {
    private ObjectiveFunction function;
    private Solution currentSolution;
    private Solution bestSolution;
    private int numberOfCities;
    private double currentResult;
    private double bestResult;

    public SimulatedAnnealing(int numberOfCities) {
        this.function = new MyObjectiveFunction();
        currentSolution = new MySolution(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        bestSolution = currentSolution;
        bestResult = function.evaluate(bestSolution,null);
    }

    @Override
    public double execute(int numberOfIterations) {
        currentResult = bestResult;
        Move move = new MySwapTwoRandomExamsMove();
        for (int i = 0; i <= numberOfIterations; i++) {
            if (currentResult < bestResult) {
                bestResult = currentResult;
                bestSolution = (MySolution)currentSolution.clone();
            } else if (Math.exp((bestResult - currentResult) / Math.sqrt(currentResult) / (1.0001 - ((i * 1.0) / numberOfIterations))) < RandomGenerator.random.nextDouble()) {
                move.undoMove(currentSolution);
            }
            currentResult = function.evaluate(currentSolution, move);

            if (i % 10000 == 0) {
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
