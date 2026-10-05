package com.algorithm.algorithm.sorting;

import java.util.Arrays;
import com.algorithm.algorithm.Algorithm;
import com.algorithm.algorithm.AlgorithmResult;
import com.algorithm.algorithm.ComplexityInfo;

public final class InsertionSort implements Algorithm {
    private static final ComplexityInfo COMPLEXITY = new ComplexityInfo(
            "Θ(n)", "Θ(n²)", "Θ(n²)", "O(1)", "Best case occurs when the input is already sorted.");
    public String getId() { return "insertionSort"; }
    public String getName() { return "Insertion Sort"; }
    public String getCategory() { return "Sort"; }
    public boolean requiresSearchKey() { return false; }
    public ComplexityInfo getComplexity() { return COMPLEXITY; }
    public AlgorithmResult execute(int[] input, Integer ignored) {
        int[] numbers = input.clone();
        for (int i = 1; i < numbers.length; i++) {
            int key = numbers[i];
            int j = i - 1;
            while (j >= 0 && numbers[j] > key) { numbers[j + 1] = numbers[j]; j--; }
            numbers[j + 1] = key;
        }
        return new AlgorithmResult(getId(), getName(), getCategory(), input, numbers,
                "Sorted Array: " + Arrays.toString(numbers), null, null, COMPLEXITY);
    }
}
