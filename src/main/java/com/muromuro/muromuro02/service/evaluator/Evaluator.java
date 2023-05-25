package com.muromuro.muromuro02.service.evaluator;

import com.muromuro.muromuro02.model.MuroMuroResponse;

/**
 * The Evaluator interface which is used to evaluate the MuroMuro solutions.
 */
public interface Evaluator {

    /**
     * Returns the initial solution for the MuroMuro question.
     * This is the placeholder solution until the user enters their own solution.
     */
    String getInitialSolution();

    /**
     * Evaluates the user's solution to the MuroMuro question.
     *
     * @param userInput The user's solution to the MuroMuro question.
     * @return The evaluation response to the user's solution.
     */
    MuroMuroResponse evaluateSolution(String userInput);
}
