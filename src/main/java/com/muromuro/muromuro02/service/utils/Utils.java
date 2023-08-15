package com.muromuro.muromuro02.service.utils;

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
     * If any of the first 3 words in the given code string is not the word "static",
     * then prepends "static" to the string.
     * TODO: Make this skip over the code comments at the beginning of the code, if possible.
     * TODO: Maybe we should use a proper Java code parser in this codebase.
     */
    public static String prependStaticIfMissing(String code) {
        if (code == null || code.isEmpty()) {
            return code;
        }
        String[] words = code.split("\\s+");
        boolean containsStatic = false;
        for (int i = 0; i < 3 && i < words.length; i++) {
            if (words[i].equals("static")) {
                containsStatic = true;
                break;
            }
        }
        if (!containsStatic) {
            return "static " + code;
        }
        return code;
    }
}
