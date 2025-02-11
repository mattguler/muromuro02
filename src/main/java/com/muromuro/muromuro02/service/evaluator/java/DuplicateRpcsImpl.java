package com.muromuro.muromuro02.service.evaluator.java;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import com.muromuro.muromuro02.model.UserInput;
import com.muromuro.muromuro02.service.docker.DockerProxy;
import com.muromuro.muromuro02.service.resourcemgmt.JavaEvaluationCode;
import com.muromuro.muromuro02.service.resourcemgmt.JavaInitialSolution;
import com.muromuro.muromuro02.service.utils.Security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.regex.Pattern;

import static com.muromuro.muromuro02.service.utils.Utils.*;

/** The evaluator for the Duplicate RPCs question. */
@Service
public class DuplicateRpcsImpl extends AbstractJavaEvaluatorImpl {

    private static final int USER_CODE_MAX_LENGTH = 5000;
    private static final String CLIENT_NAME = "KVListClient";
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

    private final JavaInitialSolution initialSolution;
 
    @Autowired
    public DuplicateRpcsImpl(
            DockerProxy dockerProxy,
            @Qualifier("duplicateRpcs") JavaEvaluationCode evaluationCode,
            @Qualifier("duplicateRpcsSoln") JavaInitialSolution initialSolution,
            @Qualifier("javaSecurityImpl") Security security) {
        super(dockerProxy, evaluationCode, security);
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
    protected JavaEvaluationCode buildEvaluationCode(UserInput userInput) {
        JavaEvaluationCode updatedEvalCode =
                refineCallerCode(userInput.getCallerCode(), evaluationCode);
        if (updatedEvalCode.getBuildResponse().getStatus()
                != MuroMuroResponse.Status.SUCCESS) {
            return updatedEvalCode;
        }
        return updatedEvalCode
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

    /**
     * Makes updates to the given caller code help simulate the test scenarios
     * for duplicating the RPCs. Makes updates to the given JavaEvaluationCode, and
     * returns the updated version.
     */
    private JavaEvaluationCode refineCallerCode(
            String callerCode, JavaEvaluationCode evalCode) {
        if (!callerCode.contains("class " + CLIENT_NAME)) {
            return evalCode.updateBuildResponse(
                    new MuroMuroResponse(
                            MuroMuroResponse.Status.FAILURE,
                            "User input does not contain the correct client name."));
        }
        String clientCode1 =
                callerCode.replace(CLIENT_NAME, CLIENT_NAME + "1");
        String clientCode2 =
                callerCode.replace(CLIENT_NAME, CLIENT_NAME + "2");
        CallerCodeUpdate dupClientCode2 = duplicateCalls(clientCode2, 2);
        if (dupClientCode2.errorMessage() != null && !dupClientCode2.errorMessage().isEmpty()) {
            return evalCode.updateBuildResponse(
                    new MuroMuroResponse(
                            MuroMuroResponse.Status.FAILURE,
                            dupClientCode2.errorMessage()));
        }
        String clientCode3 =
                callerCode.replace(CLIENT_NAME, CLIENT_NAME + "3");
        CallerCodeUpdate dupClientCode3 = duplicateCalls(clientCode3, 3);
        if (dupClientCode3.errorMessage() != null && !dupClientCode3.errorMessage().isEmpty()) {
            return evalCode.updateBuildResponse(
                    new MuroMuroResponse(
                            MuroMuroResponse.Status.FAILURE,
                            dupClientCode3.errorMessage()));
        }
        String updatedCallerCode =
                clientCode1 + "\n\n"
                        + dupClientCode2.newCallerCode() + "\n\n"
                        + dupClientCode3.newCallerCode();
        return evalCode.replaceCallerCode(updatedCallerCode);
    }

    /**
     * A tuple that contains the updated caller code, as well as an error message.
     * Usually only one of the fields is populated at any given time.
     */
    private record CallerCodeUpdate(String newCallerCode, String errorMessage) {}

    /**
     * Simulates RPC request duplication in the given code by duplicating the
     * calls to the serviceProxy object by the given number of times.
     * If not all the serviceProxy calls are not already present in the given code,
     * returns an error instead of the updated code.
     */
    private CallerCodeUpdate duplicateCalls(String callerCode, int num) {
        if (!callerCode.contains("List<V> result = serviceProxy.get(")) {
            return new CallerCodeUpdate(
                    "",
                    "User input is missing statement:\n"
                            + "List<V> result = serviceProxy.get()");
        }
        if (!callerCode.contains("serviceProxy.append(")) {
            return new CallerCodeUpdate(
                    "",
                    "User input is missing serviceProxy.append() calls.");
        }
        if (!callerCode.contains("serviceProxy.replaceAll(")) {
            return new CallerCodeUpdate(
                    "",
                    "User input is missing serviceProxy.replaceAll() calls.");
        }
        if (!callerCode.contains("serviceProxy.delete(")) {
            return new CallerCodeUpdate(
                    "",
                    "User input is missing serviceProxy.delete() calls.");
        }
        String newCallerCode = duplicateCall(callerCode, "serviceProxy.get(", num);
        newCallerCode = duplicateCall(newCallerCode, "serviceProxy.append(", num);
        newCallerCode = duplicateCall(newCallerCode, "serviceProxy.replaceAll(", num);
        newCallerCode = duplicateCall(newCallerCode, "serviceProxy.delete(", num);
        return new CallerCodeUpdate(newCallerCode, "");
    }

    /**
     * Duplicates the given call string in the given code by the given number of times.
     * The duplication takes place from the beginning of the given call string all the
     * way to the end of that particular statement, which ends in a semicolon.
     */
    private String duplicateCall(String callerCode, String call, int num) {
        int startIndex = callerCode.indexOf(call);
        int endIndex = callerCode.indexOf(";", startIndex + 1);
        String fullCall = callerCode.substring(startIndex, endIndex + 1);
        String dupCalls = fullCall.repeat(num);
        return callerCode.replace(fullCall, dupCalls);
    }
}
