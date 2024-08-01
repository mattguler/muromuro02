package com.muromuro.muromuro02.service.utils;

import com.muromuro.muromuro02.model.MuroMuroResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** The Utils class which is used to provide various utility methods. */
public class Utils {

    /**
     * Finds and replaces all the enum names in the given oldString with the replacement name.
     * Returns the updated string if successful, or the old string otherwise.
     */
    public static String replaceEnumNames(String oldString, String replacement) {
        return replaceEnumNames(oldString, replacement, 0);
    }

    private static String replaceEnumNames(String oldString, String replacement, int fromIndex) {
        String targetWord = "enum ";
        int startingIndex = oldString.indexOf(targetWord, fromIndex);
        if (startingIndex < 0) {
            return oldString;
        }
        startingIndex += targetWord.length();
        String actualTargetStr = getWordsBetween(oldString, startingIndex, '{');
        String newString = oldString;
        // Only do the replacement if the enum name is correctly formed.
        if (actualTargetStr.matches("\\S+")) {
            newString = oldString.replace(actualTargetStr, replacement);
        }
        // Recurse to replace other remaining enum names in the given string.
        return replaceEnumNames(newString, replacement, startingIndex);
    }

    /**
     * Finds and replaces all the potential word targets in the given oldString with the replacement value.
     */
    public static String replaceTargetWords(String oldString, String replacement, String... targetWords) {
        String newString = oldString;
        for (String target : targetWords) {
            newString = newString.replace(target, replacement);
        }
        return newString;
    }

    /**
     * Finds and retrieves the class name in the given Java code string.
     * Returns null if the class name cannot be found.
     */
    public static String getClassName(String code) {
        return getClassName(code, 0);
    }

    private static String getClassName(String code, int fromIndex) {
        String targetWord = "class ";
        int startingIndex = code.indexOf(targetWord, fromIndex);
        if (startingIndex < 0) {
            return null;
        }
        startingIndex += targetWord.length();
        String actualTargetStr = getWordsBetween(code, startingIndex, '{');
        // Only retrieve the class name if it is correctly formed.
        if (actualTargetStr.matches("\\S+")) {
            return actualTargetStr;
        }
        return getClassName(code, startingIndex);
    }

    /**
     * In the given code string, returns the word(s) between the given starting index
     * and the first occurrence of the given ending character.
     * Trims the whitespace from the beginning and the end of the string of words
     * before returning them. Cannot do anything about the whitespace in between the words.
     */
    private static String getWordsBetween(
            String code, int startingIndex, char endingChar) {
        StringBuilder actualTarget = new StringBuilder();
        int index = startingIndex;
        while (index < code.length()) {
            char ch = code.charAt(index);
            if (ch == endingChar) {
                break;
            }
            actualTarget.append(ch);
            index++;
        }
        String actualTargetStr = actualTarget.toString();
        actualTargetStr = actualTargetStr.trim();
        return actualTargetStr;
    }

    /**
     * Validates that the given code string does not start with any import statements.
     */
    public static MuroMuroResponse validateNotStartsWithImports(String code) {
        if (code.trim().startsWith("import ")) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.FAILURE,
                    "The code should not contain any import statements.");
        }
        return new MuroMuroResponse(MuroMuroResponse.Status.SUCCESS, "");
    }

    /**
     * Counts and returns how many times the given keyword string appears in the given code string.
     */
    public static int countKeywordOccurrences(String code, String keyword) {
        int count = 0;
        int index = 0;
        while (index < code.length()) {
            index = code.indexOf(keyword, index);
            if (index < 0) {
                break;
            }
            count++;
            index += keyword.length();
        }
        return count;
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
     * Creates a test matcher pattern to validate certain strings in the eval output.
     * Creates a list of these matcher patterns for the given format string starting from
     * the given startTestId and ending at the given endTestId (not inclusive).
     */
    public static List<Pattern> createTestMatcherPatterns(
            String formatStr, int startTestId, int endTestId) {
        List<Pattern> result = new ArrayList<>();
        for (int testId = startTestId; testId < endTestId; testId++) {
            String regex = String.format(formatStr, testId);
            Pattern pattern = Pattern.compile(regex);
            result.add(pattern);
        }
        return result;
    }

    /**
     * Returns true if the given input matches the test pattern for the given test id.
     * Returns false otherwise.
     */
    public static boolean matchesTestPattern(
            String input, List<Pattern> patterns, int testId) {
        Matcher matcher = patterns.get(testId).matcher(input);
        return matcher.find();
    }

    /**
     * Returns true if the given input matches all the test patterns in the given list.
     * Returns false otherwise.
     */
    public static boolean matchesAllTestPatterns(
            String input, List<Pattern> patterns) {
        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(input);
            if (!matcher.find()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if the given input matches any of the test patterns in the given list.
     * Returns false otherwise.
     */
    public static boolean matchesAnyTestPattern(
            String input, List<Pattern> patterns) {
        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(input);
            if (matcher.find()) {
                return true;
            }
        }
        return false;
    }
}
