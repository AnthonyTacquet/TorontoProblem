package be.odisee;

import be.odisee.data.DataReader;
import be.odisee.domain.Exam;
import be.odisee.framework.SearchAlgorithm;
import be.odisee.hillclimbing.SteepestDescent;

import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        DataReader parser = new DataReader("benchmarks/sta-f-83.crs", "benchmarks/sta-f-83.stu");
        SearchAlgorithm steepestDescent = new SteepestDescent(parser);
        steepestDescent.execute(2000); // Original: 1000
    }
}
