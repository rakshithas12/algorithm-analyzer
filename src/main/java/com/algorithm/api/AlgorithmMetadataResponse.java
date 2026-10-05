package com.algorithm.api;

import java.util.Locale;
import com.algorithm.algorithm.Algorithm;
import com.algorithm.algorithm.AlgorithmRegistry;
import com.algorithm.algorithm.ComplexityInfo;

/** Public API view of registry metadata. */
public final class AlgorithmMetadataResponse {
    private final String id;
    private final String name;
    private final String category;
    private final Complexity complexity;
    private final String notes;

    public AlgorithmMetadataResponse(Algorithm algorithm, AlgorithmRegistry registry) {
        this.id = registry.getApiId(algorithm);
        this.name = algorithm.getName();
        this.category = apiCategory(algorithm.getCategory());
        this.complexity = new Complexity(algorithm.getComplexity());
        this.notes = algorithm.getComplexity().getNotes();
    }

    private String apiCategory(String category) {
        if ("Sort".equalsIgnoreCase(category)) return "SORTING";
        if ("Search".equalsIgnoreCase(category)) return "SEARCHING";
        return category.toUpperCase(Locale.ROOT);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public Complexity getComplexity() { return complexity; }
    public String getNotes() { return notes; }

    public static final class Complexity {
        private final String best;
        private final String average;
        private final String worst;
        private final String space;

        private Complexity(ComplexityInfo info) {
            this.best = info.getBestCaseTime();
            this.average = info.getAverageCaseTime();
            this.worst = info.getWorstCaseTime();
            this.space = info.getAuxiliarySpace();
        }

        public String getBest() { return best; }
        public String getAverage() { return average; }
        public String getWorst() { return worst; }
        public String getSpace() { return space; }
    }
}
