package com.algorithm.model;

import java.util.List;

public class AlgorithmInfo {
    private final String algorithmKey;
    private final String name;
    private final String category;
    private final String description;
    private final String complexity;
    private final List<String> steps;

    public AlgorithmInfo(String algorithmKey, String name, String category, String description,
            String complexity, List<String> steps) {
        this.algorithmKey = algorithmKey;
        this.name = name;
        this.category = category;
        this.description = description;
        this.complexity = complexity;
        this.steps = steps;
    }

    public String getAlgorithmKey() {
        return algorithmKey;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getComplexity() {
        return complexity;
    }

    public List<String> getSteps() {
        return steps;
    }
}
