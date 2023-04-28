package com.muromuro.muromuro02.service.evaluator;

import com.muromuro.muromuro02.model.MuroMuroResponse;

public interface Evaluator {
    String getInitialSolution();
    MuroMuroResponse checkIfCodeSecure(String userInput);
    String buildEvaluationCode(String userInput);
    MuroMuroResponse analyzeEvaluation(String dockerEvalOutput);
}
