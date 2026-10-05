package com.algorithm.algorithm.searching;

import java.util.Arrays;
import com.algorithm.algorithm.Algorithm;
import com.algorithm.algorithm.AlgorithmResult;
import com.algorithm.algorithm.ComplexityInfo;

public final class BinarySearch implements Algorithm {
    private static final ComplexityInfo COMPLEXITY = new ComplexityInfo(
            "O(1) search", "O(log n) search", "O(log n) search", "O(1) search",
            "The search itself assumes sorted input. This application sorts first, so total operation cost also includes Arrays.sort(int[]); its cost is not represented by the search-only figures.");
    public String getId() { return "binarySearch"; }
    public String getName() { return "Binary Search"; }
    public String getCategory() { return "Search"; }
    public boolean requiresSearchKey() { return true; }
    public ComplexityInfo getComplexity() { return COMPLEXITY; }
    public AlgorithmResult execute(int[] input, Integer searchKey) {
        if (searchKey == null) throw new IllegalArgumentException("Please enter the element to search.");
        int[] numbers = input.clone();
        Arrays.sort(numbers);
        int left = 0, right = numbers.length - 1, index = -1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (numbers[mid] == searchKey) { index = mid; break; }
            if (numbers[mid] < searchKey) left = mid + 1; else right = mid - 1;
        }
        String output = index >= 0
                ? "Element " + searchKey + " found at index " + index + " in sorted array " + Arrays.toString(numbers) + "."
                : "Element " + searchKey + " not found in sorted array " + Arrays.toString(numbers) + ".";
        return new AlgorithmResult(getId(), getName(), getCategory(), input, numbers, output,
                index >= 0 ? index : null, null, COMPLEXITY);
    }
}
