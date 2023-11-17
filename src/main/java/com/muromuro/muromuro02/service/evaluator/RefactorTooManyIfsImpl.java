package com.muromuro.muromuro02.service.evaluator;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import com.muromuro.muromuro02.model.UserInput;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/** The evaluator for the RefactorTooManyIfs question. */
@Service
public class RefactorTooManyIfsImpl implements Evaluator {

    private final InitialSolution initialSolution;

    public RefactorTooManyIfsImpl(
            @Qualifier("refactorTooManyIfsSoln") InitialSolution initialSolution) {
        this.initialSolution = initialSolution;
    }

    @Override
    public UserInput getInitialSolution() {
        return new UserInput("", initialSolution.getRelevantContent());
    }

    @Override
    public MuroMuroResponse evaluateSolution(UserInput userInput) {
        return new MuroMuroResponse(
                MuroMuroResponse.Status.UNKNOWN,
                "This Muromuro question has not been implemented yet.");
    }
}
