package com.algorithm.api;

import com.algorithm.algorithm.Algorithm;
import com.algorithm.algorithm.AlgorithmRegistry;
import com.algorithm.algorithm.AlgorithmResult;

/** Structured JSON response for an algorithm execution. */
public final class ApiExecutionResponse {
    private final boolean success = true;
    private final String algorithm;
    private final int[] input;
    private final Object output;
    private final String message;
    private final AlgorithmMetadataResponse.Complexity complexity;
    private final Integer searchIndex;
    private final Integer sum;
    private final Boolean found;
    private final int[] sortedValues;

    public ApiExecutionResponse(Algorithm algorithm, AlgorithmResult result, AlgorithmRegistry registry) {
        this.algorithm = registry.getApiId(algorithm);
        this.input = result.getOriginalInput();
        this.message = result.getDisplayOutput();
        this.complexity = new AlgorithmMetadataResponse(algorithm, registry).getComplexity();
        if (algorithm.requiresSearchKey()) {
            this.found = result.getSearchIndex() != null;
            this.searchIndex = result.getSearchIndex();
            this.sum = null;
            this.output = this.found;
            this.sortedValues = "binarySearch".equals(algorithm.getId()) ? result.getOutputValues() : null;
        } else if ("addition".equals(algorithm.getId())) {
            this.found = null;
            this.searchIndex = null;
            this.sum = result.getSum();
            this.output = result.getSum();
            this.sortedValues = null;
        } else {
            this.found = null;
            this.searchIndex = null;
            this.sum = null;
            this.output = result.getOutputValues();
            this.sortedValues = null;
        }
    }

    public boolean isSuccess() { return success; }
    public String getAlgorithm() { return algorithm; }
    public int[] getInput() { return input.clone(); }
    public Object getOutput() { return output; }
    public String getMessage() { return message; }
    public AlgorithmMetadataResponse.Complexity getComplexity() { return complexity; }
    public Integer getSearchIndex() { return searchIndex; }
    public Integer getSum() { return sum; }
    public Boolean getFound() { return found; }
    public int[] getSortedValues() { return sortedValues == null ? null : sortedValues.clone(); }
}
