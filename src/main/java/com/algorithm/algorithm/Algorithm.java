package com.algorithm.algorithm;

/** Common contract for the algorithms available to the application. */
public interface Algorithm {
    String getId();
    String getName();
    String getCategory();
    boolean requiresSearchKey();
    ComplexityInfo getComplexity();
    AlgorithmResult execute(int[] numbers, Integer searchKey);
}
