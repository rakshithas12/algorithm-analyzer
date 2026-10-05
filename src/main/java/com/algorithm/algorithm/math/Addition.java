package com.algorithm.algorithm.math;

import com.algorithm.algorithm.Algorithm;
import com.algorithm.algorithm.AlgorithmResult;
import com.algorithm.algorithm.ComplexityInfo;

public final class Addition implements Algorithm {
    private static final ComplexityInfo COMPLEXITY = new ComplexityInfo(
            "O(1)", "O(1)", "O(1)", "O(1)",
            "Adds two fixed-width Java int values. Overflow retains Java int wraparound behavior.");
    public String getId() { return "addition"; }
    public String getName() { return "Addition"; }
    public String getCategory() { return "Math"; }
    public boolean requiresSearchKey() { return false; }
    public ComplexityInfo getComplexity() { return COMPLEXITY; }
    public AlgorithmResult execute(int[] numbers, Integer ignored) {
        if (numbers.length != 2) throw new IllegalArgumentException("Please enter exactly two numbers. Example: 5, 7");
        int a = numbers[0], b = numbers[1], sum = a + b;
        return new AlgorithmResult(getId(), getName(), getCategory(), numbers, new int[] { sum },
                "Add a = " + a + ", add b = " + b + ", sum = a + b = " + sum + ".",
                null, sum, COMPLEXITY);
    }
}
