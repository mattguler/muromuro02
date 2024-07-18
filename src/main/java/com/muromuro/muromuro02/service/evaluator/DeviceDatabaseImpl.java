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

import static com.muromuro.muromuro02.service.utils.Utils.buildFailureMessage;

/** The evaluator for the DeviceDatabase question. */
@Service
public class DeviceDatabaseImpl extends AbstractEvaluatorImpl {
    private static final int USER_CODE_MAX_LENGTH = 2500;

    // This is necessary for retrieving the call counts from the evaluation outputs.
    private static final int NUMBER_OF_TESTS = 4;

    // Call count matching patterns indexed according to the test IDs.
    private final List<Pattern> callCountPatterns = new ArrayList<>();

    @Autowired
    public DeviceDatabaseImpl(
            DockerProxy dockerProxy,
            @Qualifier("deviceDatabase") EvaluationCode evaluationCode) {
        super(dockerProxy, evaluationCode);
        initializeCallCountPatterns();
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

        // TODO: Do some refactoring to reduce the code duplication in the analyzeEvaluation methods.
        if (dockerEvalOutput.contains("error: not a statement")
                || dockerEvalOutput.contains("error: ';' expected")
                || dockerEvalOutput.contains("error: <identifier> expected")
                || dockerEvalOutput.contains("Error: Could not find or load main class")) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.FAILURE,
                    buildFailureMessage(
                            dockerEvalOutput,
                            "Invalid solution."));
        }
        else if (dockerEvalOutput.contains("Killed")) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.TIMEOUT,
                    "The solution took too long to execute.");
        }
        else if (dockerEvalOutput.contains("Test 1 partially working.")) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.FAILURE,
                    buildFailureMessage(
                            dockerEvalOutput,
                            "Incomplete solution.\n"
                                    + "One of the output lists is not correctly populated."));
        }
        else if (dockerEvalOutput.contains("Test 1 failed.")) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.FAILURE,
                    buildFailureMessage(
                            dockerEvalOutput,
                            "Incorrect solution."));
        }
        else if (!dockerEvalOutput.contains("Test 1 passed.")) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.UNKNOWN,
                    "Unknown server failure. "
                            + "Please contact support with the following output:\n"
                            + dockerEvalOutput);
        }
        else if (!dockerEvalOutput.contains("Test 2 passed.")) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.FAILURE,
                    buildFailureMessage(
                            dockerEvalOutput,
                            "Incorrect solution. Your solution is not\n"
                                    + "correctly handling the case of an empty input list."));
        }
        else if (!dockerEvalOutput.contains("Test 4 passed.")) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.FAILURE,
                    buildFailureMessage(
                            dockerEvalOutput,
                            "Incorrect solution. Please consider that\n"
                                    + "the device database method might return a null."));
        }
        else if (!dockerEvalOutput.contains("Test 3 passed.")) {
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
     * Initializes the call count pattern matchers that are used in the validation process.
     */
    private void initializeCallCountPatterns() {
        for (int testId = 0; testId <= NUMBER_OF_TESTS; testId++) {
            String regex = String.format("Test %d call count: (\\d+)", testId);
            Pattern pattern = Pattern.compile(regex);
            callCountPatterns.add(pattern);
        }
    }

    /**
     * Retrieves a list of call counts determined from the given dockerEvalOutput string.
     * Each call count in the list is indexed by its testId.
     * If no call count is detected for a particular testId, then 0 is returned for
     * that item in the list.
     */
    private List<Integer> retrieveCallCounts(String dockerEvalOutput) {
        List<Integer> results = new ArrayList<>();
        for (int testId = 0; testId <= NUMBER_OF_TESTS; testId++) {
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
