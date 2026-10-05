package com.algorithm.algorithm;

import static org.junit.Assert.*;
import java.util.Arrays;
import org.junit.Test;
import com.algorithm.algorithm.math.Addition;
import com.algorithm.algorithm.searching.BinarySearch;
import com.algorithm.algorithm.searching.LinearSearch;
import com.algorithm.algorithm.sorting.BubbleSort;
import com.algorithm.algorithm.sorting.InsertionSort;
import com.algorithm.algorithm.sorting.SelectionSort;

public class AlgorithmEngineTest {
    private final int[] sorted = { -3, -1, 0, 2, 2, 9 };

    @Test public void bubbleSortCoversArrayShapesAndValues() {
        assertSort(new BubbleSort());
        assertEquals("Θ(n²)", new BubbleSort().getComplexity().getBestCaseTime());
    }
    @Test public void selectionSortCoversArrayShapesAndValues() { assertSort(new SelectionSort()); }
    @Test public void insertionSortCoversArrayShapesAndValues() {
        assertSort(new InsertionSort());
        assertEquals("Θ(n)", new InsertionSort().getComplexity().getBestCaseTime());
    }
    private void assertSort(Algorithm algorithm) {
        assertArrayEquals(new int[0], algorithm.execute(new int[0], null).getOutputValues());
        assertArrayEquals(new int[] { 4 }, algorithm.execute(new int[] { 4 }, null).getOutputValues());
        assertArrayEquals(sorted, algorithm.execute(sorted, null).getOutputValues());
        assertArrayEquals(sorted, algorithm.execute(new int[] { 9, 2, 2, 0, -1, -3 }, null).getOutputValues());
        assertArrayEquals(sorted, algorithm.execute(new int[] { 2, -3, 2, 9, -1, 0 }, null).getOutputValues());
        assertArrayEquals(new int[] { -2, -1 }, algorithm.execute(new int[] { -1, -2 }, null).getOutputValues());
    }

    @Test public void linearSearchHandlesEndpointsDuplicatesAndMisses() {
        LinearSearch search = new LinearSearch();
        assertEquals(Integer.valueOf(0), search.execute(new int[] { 5, 8, 5 }, 5).getSearchIndex());
        assertEquals(Integer.valueOf(2), search.execute(new int[] { 5, 8, 9 }, 9).getSearchIndex());
        assertNull(search.execute(new int[] { 5, 8, 9 }, 4).getSearchIndex());
        assertNull(search.execute(new int[0], 4).getSearchIndex());
    }

    @Test public void binarySearchSortsBeforeSearchingAndHandlesDuplicates() {
        BinarySearch search = new BinarySearch();
        assertEquals(Integer.valueOf(0), search.execute(new int[] { 8, 3, 1 }, 1).getSearchIndex());
        assertEquals(Integer.valueOf(2), search.execute(new int[] { 8, 3, 1 }, 8).getSearchIndex());
        assertEquals(Integer.valueOf(1), search.execute(new int[] { 1, 3, 8 }, 3).getSearchIndex());
        assertNotNull(search.execute(new int[] { 2, 1, 2 }, 2).getSearchIndex());
        assertNull(search.execute(new int[] { 1, 3, 5 }, 4).getSearchIndex());
        assertNull(search.execute(new int[0], 4).getSearchIndex());
        assertTrue(search.getComplexity().getNotes().contains("sorts first"));
    }

    @Test public void additionUsesTwoIntegersAndRetainsJavaOverflow() {
        Addition addition = new Addition();
        assertEquals(Integer.valueOf(9), addition.execute(new int[] { 4, 5 }, null).getSum());
        assertEquals(Integer.valueOf(-3), addition.execute(new int[] { -7, 4 }, null).getSum());
        assertEquals(Integer.valueOf(0), addition.execute(new int[] { 0, 0 }, null).getSum());
        assertEquals(Integer.valueOf(Integer.MIN_VALUE),
                addition.execute(new int[] { Integer.MAX_VALUE, 1 }, null).getSum());
        for (int[] invalidInput : new int[][] { new int[0], new int[] { 1 }, new int[] { 1, 2, 3 } }) {
            try { addition.execute(invalidInput, null); fail("expected exact-two validation"); }
            catch (IllegalArgumentException expected) { }
        }
    }

    @Test public void registryContainsSixUniqueStableIds() {
        AlgorithmRegistry registry = AlgorithmRegistry.getDefault();
        assertEquals(6, registry.getAll().size());
        assertEquals(6, registry.getAll().stream().map(Algorithm::getId).distinct().count());
        for (String id : Arrays.asList("bubbleSort", "selectionSort", "insertionSort",
                "linearSearch", "binarySearch", "addition")) assertEquals(id, registry.get(id).getId());
        try {
            new AlgorithmRegistry(Arrays.<Algorithm>asList(new BubbleSort(), new BubbleSort()));
            fail("duplicate identifiers must be rejected");
        } catch (IllegalArgumentException expected) { }
    }

    @Test public void resultIsImmutableAndSuccessful() {
        int[] input = { 2, 1 };
        AlgorithmResult result = new BubbleSort().execute(input, null);
        input[0] = 99;
        int[] returned = result.getOriginalInput(); returned[0] = 88;
        assertArrayEquals(new int[] { 2, 1 }, result.getOriginalInput());
        assertTrue(result.isSuccess());
        assertEquals("Sorted Array: [1, 2]", result.getDisplayOutput());
    }
}
