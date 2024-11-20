package com.muromuro.muromuro02.service.evaluator;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import com.muromuro.muromuro02.model.UserInput;
import com.muromuro.muromuro02.service.docker.DockerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import static com.muromuro.muromuro02.service.utils.Security.Options.ENABLE_MULTI_THREAD_SUPPORT;
import static com.muromuro.muromuro02.service.utils.Security.checkIfCodeSecure;
import static com.muromuro.muromuro02.service.utils.Security.validateCodeLength;
import static com.muromuro.muromuro02.service.utils.Utils.validateNotStartsWithImports;

/** The evaluator for the Long Running Functions question. */
@Service
public class LongRunningFunctionsImpl extends AbstractEvaluatorImpl {

    private static final int USER_CODE_MAX_LENGTH = 5000;

    private final InitialSolution initialSolution;

    @Autowired
    public LongRunningFunctionsImpl(
            DockerProxy dockerProxy,
            @Qualifier("longRunningFunctions") EvaluationCode evaluationCode,
            @Qualifier("longRunningFunctionsSoln") InitialSolution initialSolution) {
        super(dockerProxy, evaluationCode);
        this.initialSolution = initialSolution;
    }

    @Override
    public UserInput getInitialSolution() {
        return new UserInput("", initialSolution.getRelevantContent());
    }

    @Override
    protected int getUserCodeMaxLength() {
        return USER_CODE_MAX_LENGTH;
    }

    // Overriding this method to enable the multi-thread support.
    @Override
    protected MuroMuroResponse checkIfCodeSecureAndCorrect(UserInput userInput) {
        return MuroMuroResponse.combineResponses(
                validateCodeLength(userInput, getUserCodeMaxLength()),
                checkIfCodeSecure(userInput, ENABLE_MULTI_THREAD_SUPPORT),
                validateNotStartsWithImports(userInput.getMainDefinition()));
    }

    @Override
    protected String buildEvaluationCode(UserInput userInput) {
        return evaluationCode
                .replaceMainDefinition(userInput.getMainDefinition())
                .getFormattedContent();
    }

    @Override
    protected MuroMuroResponse analyzeEvaluation(String dockerEvalOutput, UserInput userInput) {
        // TODO: Fully implement this analysis.
        return new MuroMuroResponse(
                MuroMuroResponse.Status.UNKNOWN,
                String.format(
                        "Long Running Functions evaluator not yet implemented.\n\n%s",
                        dockerEvalOutput));
    }
}
