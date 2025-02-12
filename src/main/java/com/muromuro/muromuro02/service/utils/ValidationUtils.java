package com.muromuro.muromuro02.service.utils;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class providing methods for validation operations.
 * All methods in this class help with validating the results of the Muromuro run.
 */
public class ValidationUtils {
    /**
     * Builds a message indicating to the user that the Muromuro run was successful. */
    public static String buildSuccessMessage() {
        return "The solution looks correct, but your interviewer will be the final judge.";
    }

    /**
     * Builds a failure message string by appending the given dockerEvalOutput to the
     * given error message. This is meant to ease debugging by the user.
     */
    public static String buildFailureMessage(String dockerEvalOutput, String errorMessage) {
        return errorMessage
                + "\n\nBuild and run output:\n"
                + dockerEvalOutput;
    }

    /**
     * Creates test matcher patterns to validate certain strings in the eval output.
     * Creates a map of these matcher patterns for the given format string starting from
     * the given startTestId and ending at the given endTestId (inclusive).
     * The test matcher patterns are mapped by their test IDs.
     */
    public static Map<Integer, Pattern> createTestMatcherPatterns(
            String formatStr, int startTestId, int endTestId) {
        Map<Integer, Pattern> result = new TreeMap<>();
        for (int testId = startTestId; testId <= endTestId; testId++) {
            String regex = String.format(formatStr, testId);
            Pattern pattern = Pattern.compile(regex);
            result.put(testId, pattern);
        }
        return result;
    }

    /**
     * Returns true if the given input matches the test pattern for the given test id.
     * Returns false otherwise.
     */
    public static boolean matchesTestPattern(
            String input, Map<Integer, Pattern> patterns, int testId) {
        Matcher matcher = patterns.get(testId).matcher(input);
        return matcher.find();
    }

    /**
     * Returns true if the given input matches all the test patterns in the given map.
     * Returns false otherwise.
     */
    public static boolean matchesAllTestPatterns(
            String input, Map<Integer, Pattern> patterns) {
        for (Pattern pattern : patterns.values()) {
            Matcher matcher = pattern.matcher(input);
            if (!matcher.find()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if the given input matches any of the test patterns in the given map.
     * Returns false otherwise.
     */
    public static boolean matchesAnyTestPattern(
            String input, Map<Integer, Pattern> patterns) {
        for (Pattern pattern : patterns.values()) {
            Matcher matcher = pattern.matcher(input);
            if (matcher.find()) {
                return true;
            }
        }
        return false;
    }
}
