package com.algorithm.service;

import static org.junit.Assert.*;
import org.junit.Test;
import com.algorithm.algorithm.AlgorithmResult;

public class AlgorithmServiceTest {
    private final AlgorithmService service = new AlgorithmService();

    @Test public void executesAndFormatsExistingSortAndSearchFlows() {
        AlgorithmResult sort = service.execute("bubbleSort", "8, 3, 5, 1, 9", null);
        assertEquals("Sorted Array: [1, 3, 5, 8, 9]", sort.getDisplayOutput());
        assertEquals("Element 5 found at index 2 in sorted array [1, 3, 5, 8, 9].",
                service.execute("binarySearch", "8, 3, 5, 1, 9", "5").getDisplayOutput());
    }

    @Test public void validatesEmptyMalformedAndCommaEdgeCases() {
        assertInvalid("addition", null, null, "Please enter comma-separated numbers.");
        assertInvalid("addition", "", null, "Please enter comma-separated numbers.");
        assertInvalid("addition", "1,x", null, "Invalid number: x");
        assertInvalid("addition", "1,", null, "Numbers cannot contain empty values.");
        assertInvalid("addition", "1,,2", null, "Numbers cannot contain empty values.");
        assertInvalid("linearSearch", "1,2", "", "Please enter the element to search.");
        assertInvalid("linearSearch", "1,2", "x", "Search element must be a valid integer.");
        assertInvalid("addition", "1,2,3", null, "Please enter exactly two numbers. Example: 5, 7");
    }

    private void assertInvalid(String id, String numbers, String search, String expected) {
        try { service.execute(id, numbers, search); }
        catch (IllegalArgumentException ex) { assertEquals(expected, ex.getMessage()); return; }
        fail("expected validation error: " + expected);
    }
}
