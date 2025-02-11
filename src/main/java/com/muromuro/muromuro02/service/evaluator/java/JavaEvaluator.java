package com.muromuro.muromuro02.service.evaluator.java;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import com.muromuro.muromuro02.model.UserInput;

/**
 * The interface which is used to evaluate the Java MuroMuro solutions.
 */
public interface JavaEvaluator {

    /**
     * Returns the initial solution for the MuroMuro question.
     * This is the placeholder solution until the user enters their own solution.
     */
    UserInput getInitialSolution();

    /**
     * Evaluates the user's solution to the MuroMuro question.
     *
     * @param userInput The user's solution to the MuroMuro question.
     * @return The evaluation response to the user's solution.
     */
    MuroMuroResponse evaluateSolution(UserInput userInput);
}
