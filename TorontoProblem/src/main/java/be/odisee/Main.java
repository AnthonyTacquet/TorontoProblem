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
        //DataReader parser = new DataReader("benchmarks/sta-f-83.crs", "benchmarks/sta-f-83.stu");
        DataReader parser = new DataReader("benchmarks/car-s-91.crs", "benchmarks/car-s-91.stu"); // Takes long
        //DataReader parser = new DataReader("benchmarks/hec-s-92.crs", "benchmarks/hec-s-92.stu");
        //SearchAlgorithm algortihm = new SteepestDescent(parser);
        //SearchAlgorithm algortihm = new LateAcceptanceStrategy(parser);
        SearchAlgorithm algortihm = new SimulatedAnnealing(parser);
        algortihm.execute(2000); // Original: 1000
    }
}
