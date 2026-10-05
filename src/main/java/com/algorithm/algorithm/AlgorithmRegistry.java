package com.algorithm.algorithm;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import com.algorithm.algorithm.math.Addition;
import com.algorithm.algorithm.searching.BinarySearch;
import com.algorithm.algorithm.searching.LinearSearch;
import com.algorithm.algorithm.sorting.BubbleSort;
import com.algorithm.algorithm.sorting.InsertionSort;
import com.algorithm.algorithm.sorting.SelectionSort;

/** Single registry of executable algorithms keyed by the existing stable servlet/database IDs. */
public final class AlgorithmRegistry {
    private static final AlgorithmRegistry DEFAULT = new AlgorithmRegistry(Arrays.<Algorithm>asList(
            new BubbleSort(), new SelectionSort(), new InsertionSort(),
            new LinearSearch(), new BinarySearch(), new Addition()));
    private final Map<String, Algorithm> algorithms;

    public AlgorithmRegistry(List<Algorithm> entries) {
        Map<String, Algorithm> byId = new LinkedHashMap<String, Algorithm>();
        for (Algorithm algorithm : entries) {
            if (algorithm == null || algorithm.getId() == null || algorithm.getId().trim().isEmpty())
                throw new IllegalArgumentException("Algorithm identifiers must not be empty.");
            if (byId.put(algorithm.getId(), algorithm) != null)
                throw new IllegalArgumentException("Duplicate algorithm identifier: " + algorithm.getId());
        }
        algorithms = Collections.unmodifiableMap(byId);
    }

    public static AlgorithmRegistry getDefault() { return DEFAULT; }
    public Algorithm get(String id) {
        Algorithm algorithm = algorithms.get(id);
        if (algorithm == null) throw new IllegalArgumentException("Please select a valid algorithm.");
        return algorithm;
    }
    public Algorithm getByApiId(String apiId) {
        if (apiId != null) {
            for (Algorithm algorithm : algorithms.values()) {
                if (getApiId(algorithm).equals(apiId)) return algorithm;
            }
        }
        throw new IllegalArgumentException("Unknown algorithm.");
    }
    public String getApiId(Algorithm algorithm) {
        return algorithm.getId().replaceAll("([a-z0-9])([A-Z])", "$1-$2").toLowerCase(Locale.ROOT);
    }
    public List<Algorithm> getAll() { return Collections.unmodifiableList(Arrays.asList(algorithms.values().toArray(new Algorithm[0]))); }
}
