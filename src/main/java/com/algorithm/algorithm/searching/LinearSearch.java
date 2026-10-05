package com.algorithm.algorithm.searching;

import java.util.Arrays;
import com.algorithm.algorithm.Algorithm;
import com.algorithm.algorithm.AlgorithmResult;
import com.algorithm.algorithm.ComplexityInfo;

public final class LinearSearch implements Algorithm {
    private static final ComplexityInfo COMPLEXITY = new ComplexityInfo(
            "O(1)", "Depends on target/input distribution", "O(n)", "O(1)",
            "Returns the first matching index; average cost depends on where (or whether) the target occurs.");
    public String getId() { return "linearSearch"; }
    public String getName() { return "Linear Search"; }
    public String getCategory() { return "Search"; }
    public boolean requiresSearchKey() { return true; }
    public ComplexityInfo getComplexity() { return COMPLEXITY; }
    public AlgorithmResult execute(int[] numbers, Integer searchKey) {
        if (searchKey == null) throw new IllegalArgumentException("Please enter the element to search.");
        int index = -1;
        for (int i = 0; i < numbers.length; i++) if (numbers[i] == searchKey) { index = i; break; }
        String output = index >= 0 ? "Element " + searchKey + " found at index " + index + "."
                : "Element " + searchKey + " not found.";
        return new AlgorithmResult(getId(), getName(), getCategory(), numbers, numbers, output,
                index >= 0 ? index : null, null, COMPLEXITY);
    }
}
