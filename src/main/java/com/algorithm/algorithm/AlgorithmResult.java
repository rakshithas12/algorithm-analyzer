package com.algorithm.algorithm;

import java.util.Arrays;

/** Immutable output of one algorithm invocation. No timing metrics are fabricated. */
public final class AlgorithmResult {
    private final String algorithmId;
    private final String algorithmName;
    private final String category;
    private final int[] originalInput;
    private final int[] outputValues;
    private final String displayOutput;
    private final Integer searchIndex;
    private final Integer sum;
    private final ComplexityInfo complexity;
    private final boolean success;

    public AlgorithmResult(String algorithmId, String algorithmName, String category,
            int[] originalInput, int[] outputValues, String displayOutput,
            Integer searchIndex, Integer sum, ComplexityInfo complexity) {
        this.algorithmId = algorithmId;
        this.algorithmName = algorithmName;
        this.category = category;
        this.originalInput = originalInput.clone();
        this.outputValues = outputValues.clone();
        this.displayOutput = displayOutput;
        this.searchIndex = searchIndex;
        this.sum = sum;
        this.complexity = complexity;
        this.success = true;
    }

    public String getAlgorithmId() { return algorithmId; }
    public String getAlgorithmName() { return algorithmName; }
    public String getCategory() { return category; }
    public int[] getOriginalInput() { return originalInput.clone(); }
    public int[] getOutputValues() { return outputValues.clone(); }
    public String getDisplayOutput() { return displayOutput; }
    public Integer getSearchIndex() { return searchIndex; }
    public Integer getSum() { return sum; }
    public ComplexityInfo getComplexity() { return complexity; }
    public boolean isSuccess() { return success; }

    @Override
    public String toString() {
        return algorithmName + ": " + displayOutput + " (input " + Arrays.toString(originalInput) + ")";
    }
}
