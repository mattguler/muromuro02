package com.muromuro.muromuro02.service.evaluator;

/**
 * Represents the code that is used to evaluate a solution.
 * The actual eval code is read from a resource file, and then its relevant parts
 * are replaced by the user-input solutions. Then the resulting code is sent to
 * the remote Docker container to run an evaluation of the user solution.
 */
public class EvaluationCode {

    private final String content;

    public EvaluationCode(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
