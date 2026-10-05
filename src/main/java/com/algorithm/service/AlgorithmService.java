package com.algorithm.service;

import com.algorithm.algorithm.Algorithm;
import com.algorithm.algorithm.AlgorithmRegistry;
import com.algorithm.algorithm.AlgorithmResult;

/** Parses the existing form input and delegates execution to the registered algorithm. */
public final class AlgorithmService {
    private final AlgorithmRegistry registry;

    public AlgorithmService() { this(AlgorithmRegistry.getDefault()); }
    public AlgorithmService(AlgorithmRegistry registry) { this.registry = registry; }
    public AlgorithmRegistry getRegistry() { return registry; }

    public AlgorithmResult execute(String algorithmId, String numbersInput, String searchInput) {
        Algorithm algorithm = registry.get(algorithmId);
        int[] numbers = parseNumbers(numbersInput);
        Integer searchKey = null;
        if (algorithm.requiresSearchKey())
            searchKey = parseSearchKey(searchInput);
        return algorithm.execute(numbers, searchKey);
    }

    /** Executes already-validated JSON values through the same registered engine. */
    public AlgorithmResult execute(String algorithmId, int[] values, Integer target) {
        Algorithm algorithm = registry.get(algorithmId);
        if (values == null || values.length == 0)
            throw new IllegalArgumentException("Field 'values' must be a non-empty array.");
        if (algorithm.requiresSearchKey() && target == null)
            throw new IllegalArgumentException("Field 'target' is required for search algorithms.");
        if (!algorithm.requiresSearchKey() && target != null)
            throw new IllegalArgumentException("Field 'target' is only valid for search algorithms.");
        return algorithm.execute(values, target);
    }

    private int[] parseNumbers(String input) {
        if (input == null || input.trim().isEmpty())
            throw new IllegalArgumentException("Please enter comma-separated numbers.");
        String[] parts = input.split(",", -1);
        int[] numbers = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            String value = parts[i].trim();
            if (value.isEmpty()) throw new IllegalArgumentException("Numbers cannot contain empty values.");
            try { numbers[i] = Integer.parseInt(value); }
            catch (NumberFormatException ex) { throw new IllegalArgumentException("Invalid number: " + value); }
        }
        return numbers;
    }

    private int parseSearchKey(String input) {
        if (input == null || input.trim().isEmpty())
            throw new IllegalArgumentException("Please enter the element to search.");
        try { return Integer.parseInt(input.trim()); }
        catch (NumberFormatException ex) { throw new IllegalArgumentException("Search element must be a valid integer."); }
    }
}
