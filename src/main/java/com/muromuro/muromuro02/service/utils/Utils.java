package com.muromuro.muromuro02.service.utils;

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
        StringBuilder actualTarget = new StringBuilder();
        int index = startingIndex;
        while (index < oldString.length()) {
            char ch = oldString.charAt(index);
            if (ch == '{') {
                break;
            }
            actualTarget.append(ch);
            index++;
        }
        String actualTargetStr = actualTarget.toString();
        actualTargetStr = actualTargetStr.trim();
        if (index == startingIndex) {
            return oldString;
        }
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
}
