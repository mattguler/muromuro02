package com.muromuro.muromuro02.service.evaluator;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import com.muromuro.muromuro02.model.UserInput;
import com.muromuro.muromuro02.service.docker.DockerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import static com.muromuro.muromuro02.service.utils.Utils.buildFailureMessage;

/** The evaluator for the Duplicate RPCs question. */
@Service
public class DuplicateRpcsImpl extends AbstractEvaluatorImpl {

    private static final int USER_CODE_MAX_LENGTH = 5000;

    private final InitialSolution initialSolution;

    @Autowired
    public DuplicateRpcsImpl(
            DockerProxy dockerProxy,
            @Qualifier("duplicateRpcs") EvaluationCode evaluationCode,
            @Qualifier("duplicateRpcsSoln") InitialSolution initialSolution) {
        super(dockerProxy, evaluationCode);
        this.initialSolution = initialSolution;
    }

    @Override
    public UserInput getInitialSolution() {
        return new UserInput(
                initialSolution.getCallerCode(),
                initialSolution.getMainDefinition());
    }

    @Override
    protected int getUserCodeMaxLength() {
        return USER_CODE_MAX_LENGTH;
    }

    @Override
    protected String buildEvaluationCode(UserInput userInput) {
        return evaluationCode
                .replaceCallerCode(userInput.getCallerCode())
                .replaceMainDefinition(userInput.getMainDefinition())
                .getFormattedContent();
    }

    @Override
    protected MuroMuroResponse analyzeEvaluation(
            String dockerEvalOutput, UserInput userInput) {
        // TODO: Fully implement this analysis.
        return new MuroMuroResponse(
                MuroMuroResponse.Status.UNKNOWN,
                buildFailureMessage(
                        dockerEvalOutput,
                        "Duplicate RPCs evaluator not yet implemented."));
    }
}
