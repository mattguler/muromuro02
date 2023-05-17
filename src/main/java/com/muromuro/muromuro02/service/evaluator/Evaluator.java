package com.muromuro.muromuro02.service.evaluator;

import com.muromuro.muromuro02.model.MuroMuroResponse;

public interface Evaluator {
    String getInitialSolution();
    MuroMuroResponse evaluateSolution(String userInput);
}
