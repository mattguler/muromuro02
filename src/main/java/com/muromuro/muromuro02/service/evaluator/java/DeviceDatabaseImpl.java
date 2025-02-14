package com.muromuro.muromuro02.service.evaluator.java;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import com.muromuro.muromuro02.model.UserInput;
import com.muromuro.muromuro02.service.docker.DockerProxy;
import com.muromuro.muromuro02.service.resourcemgmt.JavaEvaluationCode;
import com.muromuro.muromuro02.service.utils.CodeUtils;
import com.muromuro.muromuro02.service.utils.Security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.muromuro.muromuro02.service.utils.ValidationUtils.*;

/** The evaluator for the DeviceDatabase question. */
@Service
public class DeviceDatabaseImpl extends AbstractJavaEvaluatorImpl {

    private static final int USER_CODE_MAX_LENGTH = 2500;
    private static final int NUMBER_OF_TESTS = 4;

    private static final String INITIAL_CALLER_CODE =
            """
                    // This is the caller code that is supposed to call your own function's API.
                    // Please pass in the given inputDevices list and the ddb proxy object to
                    // your function. Populate the result lists with the devices that exist and
                    // do not exist in the ddb.
                    public void callerFunction(
                        List<String> inputDevices,
                        DeviceDatabase ddb,
                        List<String> resultDevicesInDdb,
                        List<String> resultDevicesNotInDdb
                    ) {
                        // Make call to your function here.
                    }
                    """;

    private static final String INITIAL_MAIN_DEFINITION =
            """
                    // Please implement your function here. Also define here any other data
                    // structures or helper functions that you might need.
                    """;

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

    private final Map<Integer, Pattern> partiallyWorkingTestPatterns =
            createTestMatcherPatterns(
                    "Test %d partially working",
                    /* startTestId= */ 1,
                    /* endTestId= */ NUMBER_OF_TESTS);

    // Call count matching patterns indexed according to the test IDs.
    private final Map<Integer, Pattern> callCountPatterns =
            createTestMatcherPatterns(
                    "Test %d call count: (\\d+)",
                    /* startTestId= */ 1,
                    /* endTestId= */ NUMBER_OF_TESTS);

    @Autowired
    public DeviceDatabaseImpl(
            @Qualifier("javaDockerProxyImpl") DockerProxy dockerProxy,
            @Qualifier("deviceDatabase") JavaEvaluationCode evaluationCode,
            @Qualifier("javaCodeUtilsImpl") CodeUtils codeUtils,
            @Qualifier("javaSecurityImpl") Security security) {
        super(dockerProxy, evaluationCode, codeUtils, security);
    }

    @Override
    public UserInput getInitialSolution() {
        return new UserInput(INITIAL_CALLER_CODE, INITIAL_MAIN_DEFINITION);
    }

    @Override
    protected int getUserCodeMaxLength() {
        return USER_CODE_MAX_LENGTH;
    }

    @Override
    protected JavaEvaluationCode buildEvaluationCode(UserInput userInput) {
        return evaluationCode
                .replaceCallerCode(userInput.getCallerCode())
                .replaceMainDefinition(userInput.getMainDefinition());
    }

    @Override
    protected MuroMuroResponse analyzeEvaluation(
            String dockerEvalOutput, UserInput userInput) {
        MuroMuroResponse initialAnalysis = analyzeForBasicErrors(dockerEvalOutput);
        if (initialAnalysis.getStatus() != MuroMuroResponse.Status.SUCCESS) {
            return initialAnalysis;
        }
        if (
                matchesTestPattern(
                        dockerEvalOutput, partiallyWorkingTestPatterns, /* testId= */ 1)) {
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
        Map<Integer, Integer> callCounts = retrieveCallCounts(dockerEvalOutput);
        if (callCounts.get(/* testId= */ 1) > 3
                || callCounts.get(/* testId= */ 3) > 3
                || callCounts.get(/* testId= */ 4) > 3) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.FAILURE,
                    buildFailureMessage(
                            dockerEvalOutput,
                            "Inefficient solution. While the solution output looks\n"
                                    + "correct, it can still be improved to run faster."));
        }

        return new MuroMuroResponse(
                MuroMuroResponse.Status.SUCCESS, buildSuccessMessage());
    }

    /**
     * Retrieves call counts determined from the given dockerEvalOutput string.
     * Each call count in the returned map is indexed by its testId.
     * If no call count is detected for a particular testId, then 0 is returned for
     * that item in the map.
     */
    private Map<Integer, Integer> retrieveCallCounts(String dockerEvalOutput) {
        Map<Integer, Integer> results = new TreeMap<>();
        for (int testId = 1; testId <= NUMBER_OF_TESTS; testId++) {
            Matcher matcher = callCountPatterns.get(testId).matcher(dockerEvalOutput);
            if (!matcher.find()) {
                results.put(testId, 0);
                continue;
            }
            String callCountStr = matcher.group(1);
            results.put(testId, Integer.parseInt(callCountStr));
        }
        return results;
    }
}
