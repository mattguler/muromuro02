package com.muromuro.muromuro02.service.evaluator;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import com.muromuro.muromuro02.model.UserInput;
import com.muromuro.muromuro02.service.docker.DockerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import static com.muromuro.muromuro02.service.utils.Utils.buildFailureMessage;
import static com.muromuro.muromuro02.service.utils.Utils.prependStaticIfMissing;

/** The evaluator for the DeviceDatabase question. */
@Service
public class DeviceDatabaseImpl extends AbstractEvaluatorImpl {
    private static final int USER_CODE_MAX_LENGTH = 2500;

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
        String callerCode = userInput.getCallerCode();
        String mainDefinition = userInput.getMainDefinition();
        // TODO: Get rid of this prepend-static feature.
        mainDefinition = prependStaticIfMissing(mainDefinition);
        return evaluationCode
                .replaceCallerCode(callerCode)
                .replaceMainDefinition(mainDefinition)
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

        // TODO: Analyze the call counts here as well.

        return new MuroMuroResponse(
                MuroMuroResponse.Status.SUCCESS,
                "The solution looks correct, but your interviewer "
                        + "will be the final judge.");
    }
}
