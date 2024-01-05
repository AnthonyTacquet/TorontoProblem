package be.odisee;

import be.odisee.data.DataReader;
import be.odisee.domain.Exam;
import be.odisee.framework.SearchAlgorithm;
import be.odisee.hillclimbing.SteepestDescent;
import be.odisee.lateacceptance.LateAcceptanceStrategy;
import be.odisee.simulatedannealing.SimulatedAnnealing;

import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        DataReader parser = new DataReader("benchmarks/sta-f-83.crs", "benchmarks/sta-f-83.stu");
        // DataReader parser = new DataReader("benchmarks/lse-f-91.crs", "benchmarks/lse-f-91.stu");
        SearchAlgorithm steepestDescent = new SteepestDescent(parser);
        //SearchAlgorithm lateAcceptance = new LateAcceptanceStrategy(parser);
        //SearchAlgorithm simulatedAnnealing = new SimulatedAnnealing(parser);
        steepestDescent.execute(1500); // Original: 1000
    }
}
