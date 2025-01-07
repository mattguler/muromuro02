package com.muromuro.muromuro02.service.evaluator;

import com.muromuro.muromuro02.model.MuroMuroResponse;

/**
 * Represents the code that is used to evaluate a solution.
 * The actual eval code is read from a resource file, and then its relevant parts
 * are replaced by the user-input solutions. Then the resulting code is sent to
 * the remote Docker container to run an evaluation of the user solution.
 */
public class EvaluationCode {

    private static final String CALLER_CODE_START = "// Start caller code implementation.";
    private static final String CALLER_CODE_END = "// End caller code implementation.";
    private static final String MAIN_DEFINITION_START = "// Start main definition implementation.";
    private static final String MAIN_DEFINITION_END = "// End main definition implementation.";

    // Represents the string content of the evaluation code.
    private final String content;

    // Represents the build response status of the evaluation code.
    // It has a default value of success, until the eval code build fails for some reason.
    private final MuroMuroResponse buildResponse;

    public EvaluationCode(String content) {
        this.content = content;
        this.buildResponse = new MuroMuroResponse(MuroMuroResponse.Status.SUCCESS);
    }

    public EvaluationCode(String content, MuroMuroResponse buildResponse) {
        this.content = content;
        this.buildResponse = buildResponse;
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

    /** Returns the build response status of the evaluation code. */
    public MuroMuroResponse getBuildResponse() {
        return this.buildResponse;
    }

    /**
     * Updates the build response status of the evaluation code.
     * Returns a newly built EvaluationCode instance with the updated response status.
     */
    public EvaluationCode updateBuildResponse(MuroMuroResponse buildResponse) {
        return new EvaluationCode(this.content, buildResponse);
    }

    /** Replaces the caller code in the evaluation code with the given string. */
    public EvaluationCode replaceCallerCode(String callerCode) {
        return new EvaluationCode(
                replaceSection(
                        content, CALLER_CODE_START, CALLER_CODE_END, callerCode),
                this.buildResponse);
    }

    /** Replaces the main definition in the evaluation code with the given string. */
    public EvaluationCode replaceMainDefinition(String mainDefinition) {
        return new EvaluationCode(
                replaceSection(
                        content,
                        MAIN_DEFINITION_START,
                        MAIN_DEFINITION_END,
                        mainDefinition),
                this.buildResponse);
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
