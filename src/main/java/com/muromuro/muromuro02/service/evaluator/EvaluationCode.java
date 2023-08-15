package com.muromuro.muromuro02.service.evaluator;

/**
 * Represents the code that is used to evaluate a solution.
 * The actual eval code is read from a resource file, and then its relevant parts
 * are replaced by the user-input solutions. Then the resulting code is sent to
 * the remote Docker container to run an evaluation of the user solution.
 */
class EvaluationCode {

    private static final String CALLER_CODE_START = "// Start caller code implementation.";
    private static final String CALLER_CODE_END = "// End caller code implementation.";
    private static final String MAIN_DEFINITION_START = "// Start main definition implementation.";
    private static final String MAIN_DEFINITION_END = "// End main definition implementation.";

    private final String content;

    public EvaluationCode(String content) {
        this.content = content;
    }

    /**
     * Returns the content of the evaluation code where certain characters are escaped.
     * This will enable the evaluation code to be run properly on the remote Docker container.
     */
    public String getFormattedContent() {
        String formattedContent = content.replace("\"", "\\\"");
        formattedContent = formattedContent.replace("\\n", "\\\\\\n");
        return formattedContent;
    }

    /** Replaces the caller code in the evaluation code with the given string. */
    public EvaluationCode replaceCallerCode(String callerCode) {
        return new EvaluationCode(
                replaceSection(
                        content, CALLER_CODE_START, CALLER_CODE_END, callerCode));
    }

    /** Replaces the main definition in the evaluation code with the given string. */
    public EvaluationCode replaceMainDefinition(String mainDefinition) {
        return new EvaluationCode(
                replaceSection(
                        content,
                        MAIN_DEFINITION_START,
                        MAIN_DEFINITION_END,
                        mainDefinition));
    }

    /**
     * Given a content string, replace the subsection between the beginningSequence
     * and endSequence with the replacement string.
     */
    private static String replaceSection(
            String content, String beginningSequence, String endSequence, String replacement) {
        int beginningIndex = content.indexOf(beginningSequence);
        int endIndex = content.indexOf(endSequence);
        if (beginningIndex == -1 || endIndex == -1) {
            throw new IllegalArgumentException("Could not find the beginning or end sequence.");
        }
        return content.substring(0, beginningIndex)
                + replacement
                + content.substring(endIndex + endSequence.length());
    }
}
