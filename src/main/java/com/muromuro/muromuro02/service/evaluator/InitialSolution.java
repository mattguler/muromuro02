package com.muromuro.muromuro02.service.evaluator;

/**
 * Represents the initial solution to a MuroMuro question.
 * Its content is read from a resource file, and then its relevant parts
 * are served to the user.
 */
public class InitialSolution {

    private static final String INITIAL_CALLER_CODE_START =
            "// Start initial caller code implementation.";
    private static final String INITIAL_CALLER_CODE_END =
            "// End initial caller code implementation.";
    private static final String INITIAL_MAIN_DEF_START =
            "// Start initial main definition implementation.";
    private static final String INITIAL_MAIN_DEF_END =
            "// End initial main definition implementation.";

    private final String content;

    public InitialSolution(String content) {
        this.content = content;
    }

    /** Returns the content of the initial caller code. */
    public String getCallerCode() {
        int beginningIndex = content.indexOf(INITIAL_CALLER_CODE_START);
        int endIndex = content.indexOf(INITIAL_CALLER_CODE_END);
        if (beginningIndex == -1 || endIndex == -1) {
            throw new IllegalArgumentException(
                    "Could not find the beginning or end of caller code.");
        }
        return content.substring(
                beginningIndex + INITIAL_CALLER_CODE_START.length(), endIndex);
    }

    /** Returns the content of the initial main definition. */
    public String getMainDefinition() {
        int beginningIndex = content.indexOf(INITIAL_MAIN_DEF_START);
        int endIndex = content.indexOf(INITIAL_MAIN_DEF_END);
        if (beginningIndex == -1 || endIndex == -1) {
            throw new IllegalArgumentException(
                    "Could not find the beginning or end of main def.");
        }
        return content.substring(
                beginningIndex + INITIAL_MAIN_DEF_START.length(), endIndex);
    }
}
