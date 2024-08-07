package com.muromuro.muromuro02.service.evaluator;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import com.muromuro.muromuro02.model.UserInput;
import com.muromuro.muromuro02.service.docker.DockerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.muromuro.muromuro02.service.utils.Utils.*;

/** The evaluator for the DeviceDatabase question. */
@Service
public class DeviceDatabaseImpl extends AbstractEvaluatorImpl {
    private static final int USER_CODE_MAX_LENGTH = 2500;

    // While we don't really have test index 0, we still include it in the count
    // to make the code simpler and easier to understand.
    private static final int NUMBER_OF_TESTS = 5;

    private final List<Pattern> passingTestPatterns =
            createTestMatcherPatterns(
                    "Test %d passed",
                    /* startTestId= */ 0,
                    /* endTestId= */ NUMBER_OF_TESTS);

    private final List<Pattern> failingTestPatterns =
            createTestMatcherPatterns(
                    "Test %d failed",
                    /* startTestId= */ 0,
                    /* endTestId= */ NUMBER_OF_TESTS);

    private final List<Pattern> partiallyWorkingTestPatterns =
            createTestMatcherPatterns(
                    "Test %d partially working",
                    /* startTestId= */ 0,
                    /* endTestId= */ NUMBER_OF_TESTS);

    // Call count matching patterns indexed according to the test IDs.
    private final List<Pattern> callCountPatterns =
            createTestMatcherPatterns(
                    "Test %d call count: (\\d+)",
                    /* startTestId= */ 0,
                    /* endTestId= */ NUMBER_OF_TESTS);

    @Autowired
    public DeviceDatabaseImpl(
            DockerProxy dockerProxy,
            @Qualifier("deviceDatabase") EvaluationCode evaluationCode) {
        super(dockerProxy, evaluationCode);
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
        MuroMuroResponse initialAnalysis = analyzeForBasicErrors(dockerEvalOutput);
        if (initialAnalysis.getStatus() != MuroMuroResponse.Status.SUCCESS) {
            return initialAnalysis;
        }
        if (dockerEvalOutput.contains("Test 1 partially working.")) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.FAILURE,
                    buildFailureMessage(
                            dockerEvalOutput,
                            "Incomplete solution.\n"
                                    + "One of the output lists is not correctly populated."));
        }
        else if (
                matchesTestPattern(
                        dockerEvalOutput, failingTestPatterns, /* testId= */ 1)) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.FAILURE,
                    buildFailureMessage(
                            dockerEvalOutput,
                            "Incorrect solution."));
        }
        else if (
                !matchesTestPattern(
                        dockerEvalOutput, passingTestPatterns, /* testId= */ 1)) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.UNKNOWN,
                    "Unknown server failure. "
                            + "Please contact support with the following output:\n"
                            + dockerEvalOutput);
        }
        else if (
                !matchesTestPattern(
                        dockerEvalOutput, passingTestPatterns, /* testId= */ 2)) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.FAILURE,
                    buildFailureMessage(
                            dockerEvalOutput,
                            "Incorrect solution. Your solution is not\n"
                                    + "correctly handling the case of an empty input list."));
        }
        else if (
                !matchesTestPattern(
                        dockerEvalOutput, passingTestPatterns, /* testId= */ 4)) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.FAILURE,
                    buildFailureMessage(
                            dockerEvalOutput,
                            "Incorrect solution. Please consider that\n"
                                    + "the device database method might return a null."));
        }
        else if (
                !matchesTestPattern(
                        dockerEvalOutput, passingTestPatterns, /* testId= */ 3)) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.FAILURE,
                    buildFailureMessage(
                            dockerEvalOutput,
                            "Incorrect solution. Please consider that\n"
                                    + "the input might be badly formed."));
        }

        // Analyzing the call counts.
        List<Integer> callCounts = retrieveCallCounts(dockerEvalOutput);
        if (callCounts.get(1) > 3 || callCounts.get(3) > 3 || callCounts.get(4) > 3) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.FAILURE,
                    buildFailureMessage(
                            dockerEvalOutput,
                            "Inefficient solution. While the solution output looks\n"
                                    + "correct, it can still be improved to run faster."));
        }

        return new MuroMuroResponse(
                MuroMuroResponse.Status.SUCCESS,
                "The solution looks correct, but your interviewer "
                        + "will be the final judge.");
    }

    /**
     * Retrieves a list of call counts determined from the given dockerEvalOutput string.
     * Each call count in the list is indexed by its testId.
     * If no call count is detected for a particular testId, then 0 is returned for
     * that item in the list.
     */
    private List<Integer> retrieveCallCounts(String dockerEvalOutput) {
        List<Integer> results = new ArrayList<>();
        for (int testId = 0; testId < NUMBER_OF_TESTS; testId++) {
            Matcher matcher = callCountPatterns.get(testId).matcher(dockerEvalOutput);
            if (!matcher.find()) {
                results.add(0);
                continue;
            }
            String callCountStr = matcher.group(1);
            results.add(Integer.parseInt(callCountStr));
        }
        return results;
    }
}
