package com.muromuro.muromuro02.service.evaluator;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import com.muromuro.muromuro02.model.UserInput;
import com.muromuro.muromuro02.service.docker.DockerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.regex.Pattern;

import static com.muromuro.muromuro02.service.utils.Security.Options.ENABLE_MULTI_THREAD_SUPPORT;
import static com.muromuro.muromuro02.service.utils.Security.checkIfCodeSecure;
import static com.muromuro.muromuro02.service.utils.Security.validateCodeLength;
import static com.muromuro.muromuro02.service.utils.Utils.*;

/** The evaluator for the Long Running Functions question. */
@Service
public class LongRunningFunctionsImpl extends AbstractEvaluatorImpl {

    private static final int USER_CODE_MAX_LENGTH = 5000;
    private static final int NUMBER_OF_TESTS = 3;

    private final InitialSolution initialSolution;

    private final Map<Integer, Pattern> passingTestPatterns =
            createTestMatcherPatterns(
                    "Test %d passed",
                    /* startTestId= */ 0,
                    /* endTestId= */ NUMBER_OF_TESTS - 1);

    private final Map<Integer, Pattern> failingTestPatterns =
            createTestMatcherPatterns(
                    "Test %d failed",
                    /* startTestId= */ 0,
                    /* endTestId= */ NUMBER_OF_TESTS - 1);

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
        return new UserInput("", initialSolution.getMainDefinition());
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
    protected EvaluationCode buildEvaluationCode(UserInput userInput) {
        return evaluationCode
                .replaceMainDefinition(userInput.getMainDefinition());
    }

    @Override
    protected MuroMuroResponse analyzeEvaluation(
            String dockerEvalOutput, UserInput userInput) {
        MuroMuroResponse initialAnalysis = analyzeForBasicErrors(dockerEvalOutput);
        if (initialAnalysis.getStatus() != MuroMuroResponse.Status.SUCCESS) {
            return initialAnalysis;
        }
        if (matchesAnyTestPattern(dockerEvalOutput, failingTestPatterns)) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.FAILURE,
                    buildFailureMessage(
                            dockerEvalOutput,
                            "Incorrect solution. Fails validation."));
        }
        else if (matchesAllTestPatterns(dockerEvalOutput, passingTestPatterns)) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.SUCCESS, buildSuccessMessage());
        }
        // We should not be reaching here.
        return new MuroMuroResponse(
                MuroMuroResponse.Status.UNKNOWN,
                buildFailureMessage(
                        dockerEvalOutput,
                        "Internal server failure.\n"
                                + "Please contact support with the following output."));
    }
}
