package com.muromuro.muromuro02.service.evaluator;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import com.muromuro.muromuro02.model.UserInput;
import com.muromuro.muromuro02.service.docker.DockerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.regex.Pattern;

import static com.muromuro.muromuro02.service.utils.Utils.*;

/** The evaluator for the Parse CSV question. */
@Service
public class ParseCsvImpl extends AbstractEvaluatorImpl {

    private static final int USER_CODE_MAX_LENGTH = 4000;
    private static final int NUMBER_OF_TESTS = 5;

    private static final String INITIAL_SOLUTION =
            """
                    public List<Account> parseCsv(String csv) {
                        // Your implementation goes here.
                    }
                    """;

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
    public ParseCsvImpl(
            DockerProxy dockerProxy,
            @Qualifier("parseCsv") EvaluationCode evaluationCode) {
        super(dockerProxy, evaluationCode);
    }

    @Override
    public UserInput getInitialSolution() {
        return new UserInput("", INITIAL_SOLUTION);
    }

    @Override
    protected int getUserCodeMaxLength() {
        return USER_CODE_MAX_LENGTH;
    }

    @Override
    protected String buildEvaluationCode(UserInput userInput) {
        return evaluationCode
                .replaceMainDefinition(userInput.getMainDefinition())
                .getFormattedContent();
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
