package com.muromuro.muromuro02.service.evaluator.java;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import com.muromuro.muromuro02.model.UserInput;
import com.muromuro.muromuro02.service.docker.DockerProxy;
import com.muromuro.muromuro02.service.resourcemgmt.JavaEvaluationCode;
import com.muromuro.muromuro02.service.resourcemgmt.JavaInitialSolution;
import com.muromuro.muromuro02.service.utils.CodeUtils;
import com.muromuro.muromuro02.service.utils.Security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.regex.Pattern;

import static com.muromuro.muromuro02.service.utils.Security.Options.ENABLE_MULTI_THREAD_SUPPORT;
import static com.muromuro.muromuro02.service.utils.ValidationUtils.*;

/** The evaluator for the Long Running Functions question. */
@Service
public class LongRunningFunctionsImpl extends AbstractJavaEvaluatorImpl {

    private static final int USER_CODE_MAX_LENGTH = 5000;
    private static final int NUMBER_OF_TESTS = 3;

    private final JavaInitialSolution initialSolution;

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
            @Qualifier("javaDockerProxyImpl") DockerProxy dockerProxy,
            @Qualifier("longRunningFunctions") JavaEvaluationCode evaluationCode,
            @Qualifier("longRunningFunctionsSoln") JavaInitialSolution initialSolution,
            @Qualifier("javaCodeUtilsImpl") CodeUtils codeUtils,
            @Qualifier("javaSecurityImpl") Security security) {
        super(dockerProxy, evaluationCode, codeUtils, security);
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
                security.validateCodeLength(userInput, getUserCodeMaxLength()),
                security.checkIfCodeSecure(userInput, ENABLE_MULTI_THREAD_SUPPORT),
                codeUtils.validateNotStartsWithImports(userInput.getMainDefinition()));
    }

    @Override
    protected JavaEvaluationCode buildEvaluationCode(UserInput userInput) {
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
