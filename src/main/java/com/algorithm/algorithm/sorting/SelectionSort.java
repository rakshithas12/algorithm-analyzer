package com.algorithm.algorithm.sorting;

import java.util.Arrays;
import com.algorithm.algorithm.Algorithm;
import com.algorithm.algorithm.AlgorithmResult;
import com.algorithm.algorithm.ComplexityInfo;

public final class SelectionSort implements Algorithm {
    private static final ComplexityInfo COMPLEXITY = new ComplexityInfo(
            "Θ(n²)", "Θ(n²)", "Θ(n²)", "O(1)", "Scans the remaining unsorted suffix on each pass.");
    public String getId() { return "selectionSort"; }
    public String getName() { return "Selection Sort"; }
    public String getCategory() { return "Sort"; }
    public boolean requiresSearchKey() { return false; }
    public ComplexityInfo getComplexity() { return COMPLEXITY; }
    public AlgorithmResult execute(int[] input, Integer ignored) {
        int[] numbers = input.clone();
        for (int i = 0; i < numbers.length - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < numbers.length; j++) {
                if (numbers[j] < numbers[minIndex]) minIndex = j;
            }
            int temp = numbers[i]; numbers[i] = numbers[minIndex]; numbers[minIndex] = temp;
        }
        return new AlgorithmResult(getId(), getName(), getCategory(), input, numbers,
                "Sorted Array: " + Arrays.toString(numbers), null, null, COMPLEXITY);
    }
}
