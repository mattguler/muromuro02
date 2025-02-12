package com.muromuro.muromuro02.service.utils;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/** Unit tests for the ValidationUtils class. */
public class ValidationUtilsTest {

    @Test
    public void testBuildSuccessMessage() {
        String message = ValidationUtils.buildSuccessMessage();
        assertEquals(
                "The solution looks correct, but your interviewer will be the final judge.",
                message);
    }

    @Test
    public void testBuildFailureMessage() {
        String errorMessage = "Error happened.";
        String dockerEvalOutput = "Various build errors.";
        String fullMessage = ValidationUtils.buildFailureMessage(dockerEvalOutput, errorMessage);
        assertEquals(
                "Error happened.\n\nBuild and run output:\nVarious build errors.",
                fullMessage);
    }

    @Test
    public void testCreateTestMatcherPatterns() {
        Map<Integer, Pattern> patterns =
                ValidationUtils.createTestMatcherPatterns(
                        "Test %d passed.", 0, 4);
        assertEquals(5, patterns.size());
        assertTrue(patterns.get(0).matcher("Test 0 passed.").find());
        assertTrue(patterns.get(1).matcher("Test 1 passed.").find());
        assertTrue(patterns.get(2).matcher("Test 2 passed.").find());
        assertTrue(patterns.get(3).matcher("Test 3 passed.").find());
        assertTrue(patterns.get(4).matcher("Test 4 passed.").find());
    }

    @Test
    public void testMatchesTestPattern() {
        Map<Integer, Pattern> patterns =
                ValidationUtils.createTestMatcherPatterns(
                        "Test %d failed.", 0, 4);
        assertEquals(5, patterns.size());
        assertTrue(
                ValidationUtils.matchesTestPattern(
                        "Test 4 failed.", patterns, 4));
        assertFalse(
                ValidationUtils.matchesTestPattern(
                        "Test 2 passed.", patterns, 2));
    }

    @Test
    public void testMatchesAllTestPatterns() {
        Map<Integer, Pattern> patterns =
                ValidationUtils.createTestMatcherPatterns(
                        "Test %d passed.", 0, 4);
        assertEquals(5, patterns.size());
        String matchingInput =
                """
                        Test 0 passed.
                        Test 1 passed.
                        Test 2 passed.
                        Test 3 passed.
                        Test 4 passed.
                        """;
        assertTrue(ValidationUtils.matchesAllTestPatterns(matchingInput, patterns));
        String nonMatchingInput =
                """
                        Test 0 passed.
                        Test 1 passed.
                        Test 2 failed.
                        Test 3 passed.
                        Test 4 passed.
                        """;
        assertFalse(ValidationUtils.matchesAllTestPatterns(nonMatchingInput, patterns));
    }

    @Test
    public void testMatchesAnyTestPattern() {
        Map<Integer, Pattern> patterns =
                ValidationUtils.createTestMatcherPatterns(
                        "Test %d failed.", 0, 4);
        assertEquals(5, patterns.size());
        String matchingInput =
                """
                        Test 0 passed.
                        Test 1 passed.
                        Test 2 failed.
                        Test 3 passed.
                        Test 4 passed.
                        """;
        assertTrue(ValidationUtils.matchesAnyTestPattern(matchingInput, patterns));
        String nonMatchingInput =
                """
                        Test 0 passed.
                        Test 1 passed.
                        Test 2 passed.
                        Test 3 passed.
                        Test 4 passed.
                        """;
        assertFalse(ValidationUtils.matchesAnyTestPattern(nonMatchingInput, patterns));
    }
}
