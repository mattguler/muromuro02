package com.muromuro.muromuro02.service.evaluator;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import com.muromuro.muromuro02.model.UserInput;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/** The evaluator for the DesignApiWithPagination question. */
@Service
public class DesignApiWithPaginationImpl implements Evaluator {

    private final EvaluationCode evaluationCode;

    public DesignApiWithPaginationImpl(
            @Qualifier("designApiWithPagination") EvaluationCode evaluationCode) {
        this.evaluationCode = evaluationCode;
    }

    @Override
    public UserInput getInitialSolution() {
        // TODO: Populate the initial solution here, as necessary.
        return new UserInput("", "");
    }

    @Override
    public MuroMuroResponse evaluateSolution(UserInput userInput) {
        // TODO: Implement a proper evaluation here.
        return new MuroMuroResponse(
                MuroMuroResponse.Status.UNKNOWN,
                "Evaluator file content, for debugging purposes:\n"
                        + evaluationCode.getContent());
    }
}
