package com.algorithm.algorithm.sorting;

import java.util.Arrays;
import com.algorithm.algorithm.Algorithm;
import com.algorithm.algorithm.AlgorithmResult;
import com.algorithm.algorithm.ComplexityInfo;

public final class BubbleSort implements Algorithm {
    private static final ComplexityInfo COMPLEXITY = new ComplexityInfo(
            "Θ(n²)", "Θ(n²)", "Θ(n²)", "O(1)",
            "This implementation performs every pass and does not stop early when the array is already sorted.");
    public String getId() { return "bubbleSort"; }
    public String getName() { return "Bubble Sort"; }
    public String getCategory() { return "Sort"; }
    public boolean requiresSearchKey() { return false; }
    public ComplexityInfo getComplexity() { return COMPLEXITY; }
    public AlgorithmResult execute(int[] input, Integer ignored) {
        int[] numbers = input.clone();
        for (int i = 0; i < numbers.length - 1; i++) {
            for (int j = 0; j < numbers.length - i - 1; j++) {
                if (numbers[j] > numbers[j + 1]) {
                    int temp = numbers[j]; numbers[j] = numbers[j + 1]; numbers[j + 1] = temp;
                }
            }
        }
        return new AlgorithmResult(getId(), getName(), getCategory(), input, numbers,
                "Sorted Array: " + Arrays.toString(numbers), null, null, COMPLEXITY);
    }
}
