package com.muromuro.muromuro02.service.utils;

import com.muromuro.muromuro02.model.MuroMuroResponse;

/**
 * Utility class interface for code string manipulation and analysis.
 * All methods in this class operate on String representations of some code
 * and perform various parsing and manipulation operations before the actual
 * compilation or execution of the code.
 */
public interface CodeUtils {

    /**
     * Finds and replaces all the enum names in the given oldString with the replacement name.
     * Returns the updated string if successful, or the old string otherwise.
     */
    String replaceEnumNames(String oldString, String replacement);

    /**
     * Finds and replaces all the potential word targets in the given oldString with the replacement value.
     */
    String replaceTargetWords(String oldString, String replacement, String... targetWords);

    /**
     * Finds and retrieves the class name in the given Java code string.
     * Returns null if the class name cannot be found.
     */
    String getClassName(String code);

    /**
     * Validates that the given code string does not start with any import statements.
     */
    MuroMuroResponse validateNotStartsWithImports(String code);

    /**
     * Counts and returns how many times the given keyword string appears in the given code string.
     */
    int countKeywordOccurrences(String code, String keyword);

}
