package com.muromuro.muromuro02.service.evaluator;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import com.muromuro.muromuro02.model.UserInput;
import com.muromuro.muromuro02.service.docker.DockerProxy;
import com.muromuro.muromuro02.service.resourcemgmt.JavaInitialSolution;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.regex.Pattern;

import static com.muromuro.muromuro02.service.utils.Utils.*;

/** The evaluator for the Incompatible Interfaces question. */
@Service
public class IncompatibleInterfacesImpl extends AbstractEvaluatorImpl {

    private static final int USER_CODE_MAX_LENGTH = 5000;
    private static final int NUMBER_OF_TESTS = 2;

    private final Map<Integer, Pattern> passingTestPatterns =
            createTestMatcherPatterns(
                    "Test %d passed",
                    /* startTestId= */ 1,
                    /* endTestId= */ NUMBER_OF_TESTS);

    private final Map<Integer, Pattern> failingTestPatterns =
            createTestMatcherPatterns(
                    "Test %d failed",
                    /* startTestId= */ 1,
                    /* endTestId= */ NUMBER_OF_TESTS);

    private final Map<Integer, Pattern> partialTestPatterns =
            createTestMatcherPatterns(
                    "Test %d failed. Offset adjustments to y values are missing.",
                    /* startTestId= */ 1,
                    /* endTestId= */ NUMBER_OF_TESTS);

    private final JavaInitialSolution initialSolution;

    public IncompatibleInterfacesImpl(
            DockerProxy dockerProxy,
            @Qualifier("incompatibleInterfaces") EvaluationCode evaluationCode,
            @Qualifier("incompatibleInterfacesSoln") JavaInitialSolution initialSolution) {
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
        if (matchesAnyTestPattern(dockerEvalOutput, partialTestPatterns)) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.FAILURE,
                    buildFailureMessage(
                            dockerEvalOutput,
                            "Getting closer. Please make sure to check whether\n"
                                    + "all offset adjustments to y values are done correctly."));
        }
        else if (matchesAnyTestPattern(dockerEvalOutput, failingTestPatterns)) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.FAILURE,
                    buildFailureMessage(
                            dockerEvalOutput,
                            "Incorrect solution. Fails validation."));
        }
        else if (matchesAllTestPatterns(dockerEvalOutput, passingTestPatterns)) {
            if (getIfCountInCode(userInput) > 0) {
                return new MuroMuroResponse(
                        MuroMuroResponse.Status.SUCCESS,
                        "However, the solution could be better "
                                + "without the if-statements.");
            }
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

    private static int getIfCountInCode(UserInput userInput) {
        String mainDef = userInput.getMainDefinition();
        return countKeywordOccurrences(mainDef, "if(")
                + countKeywordOccurrences(mainDef, "if (");
    }
}
