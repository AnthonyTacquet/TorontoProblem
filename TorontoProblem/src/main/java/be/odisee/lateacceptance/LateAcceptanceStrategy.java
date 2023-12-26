package be.odisee.lateacceptance;

import be.odisee.MyMoveOneRandomExamMove;
import be.odisee.MyObjectiveFunction;
import be.odisee.MySolution;
import be.odisee.MySwapTwoRandomExamsMove;
import be.odisee.data.DataReader;
import be.odisee.framework.Move;
import be.odisee.framework.SearchAlgorithm;
import be.odisee.framework.Solution;

import java.util.ArrayList;
import java.util.Random;

public class LateAcceptanceStrategy extends SearchAlgorithm {

    private MyObjectiveFunction function;
    private MySolution currentSolution;
    private MySolution bestSolution;
    private double currentResult;
    private double bestResult;
    private LAList lateAcceptanceList;
    private final int listLength = 5 ;

    public LateAcceptanceStrategy(DataReader dataReader) {
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
        this.lateAcceptanceList = new LAList(listLength);
    }

    @Override
    public double execute(int numberOfIterations)  {

        //om te beginnen in totaal evalueren
        currentResult = function.evaluate(currentSolution, null);
        this.lateAcceptanceList.fillList(currentResult);
        int teller = 0;
        Random random = new Random();
        Move move = new MySwapTwoRandomExamsMove();
        Move move2 = new MyMoveOneRandomExamMove();
        int moveId = 0;
        for (int i = 0; i < numberOfIterations; i++) {
            if (currentResult <= bestResult) {
                bestResult = currentResult;
                lateAcceptanceList.addToBeginOfList(currentResult);
                bestSolution = (MySolution)currentSolution.clone();
            }
            else {
                if (currentResult <= lateAcceptanceList.getLastValueInTheList()) {
                    lateAcceptanceList.addToBeginOfList(currentResult);
                }
                else {
                    lateAcceptanceList.addToBeginOfList((currentResult+bestResult)/2);
                    if (moveId == 1)
                        currentResult = function.evaluate(currentSolution, move);
                    else
                        currentResult =  function.evaluate(currentSolution, move2);
                }
            }
            moveId = random.nextInt(2) + 1;
            if (moveId == 1)
                currentResult = function.evaluate(currentSolution, move);
            else
                currentResult = function.evaluate(currentSolution, move2);
            if (i % 100 == 0) {
                System.out.println("Iteration #" + i + " " + bestResult + " " + currentResult);
                for(int j=0;j<this.lateAcceptanceList.getSize();j++){
                    System.out.print(this.lateAcceptanceList.getList()[j] + " ");
                }
                System.out.println();
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
