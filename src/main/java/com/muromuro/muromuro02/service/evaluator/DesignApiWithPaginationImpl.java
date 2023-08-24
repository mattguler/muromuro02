package com.muromuro.muromuro02.service.evaluator;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import com.muromuro.muromuro02.model.UserInput;
import com.muromuro.muromuro02.service.docker.DockerProxy;
import com.muromuro.muromuro02.service.utils.Security;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import static com.muromuro.muromuro02.service.utils.Utils.prependStaticIfMissing;

/** The evaluator for the DesignApiWithPagination question. */
@Service
public class DesignApiWithPaginationImpl implements Evaluator {
    private static final int USER_CODE_MAX_LENGTH = 2500;

    private final DockerProxy dockerProxy;
    private final EvaluationCode evaluationCode;

    public DesignApiWithPaginationImpl(
            DockerProxy dockerProxy,
            @Qualifier("designApiWithPagination") EvaluationCode evaluationCode) {
        this.dockerProxy = dockerProxy;
        this.evaluationCode = evaluationCode;
    }

    @Override
    public UserInput getInitialSolution() {
        return new UserInput("", "");
    }

    @Override
    public MuroMuroResponse evaluateSolution(UserInput userInput) {
        MuroMuroResponse securityResponse = checkIfCodeSecureAndCorrect(userInput);
        if (securityResponse.getStatus() != MuroMuroResponse.Status.SUCCESS) {
            return securityResponse;
        }
        String evalCode = buildEvaluationCode(userInput);
        String containerId = dockerProxy.startContainer(evalCode);
        String dockerEvalOutput = dockerProxy.getContainerOutput(containerId);
        // Note: Comment this out when debugging the container logs.
        dockerProxy.cleanUpContainer(containerId);
        return analyzeEvaluation(dockerEvalOutput);
    }

    private static MuroMuroResponse checkIfCodeSecureAndCorrect(UserInput userInput) {
        return MuroMuroResponse.combineResponses(
                Security.validateCodeLength(userInput, USER_CODE_MAX_LENGTH),
                Security.checkIfCodeSecure(userInput),
                checkIfCodeCorrectlyFormed(userInput));
    }

    private static MuroMuroResponse checkIfCodeCorrectlyFormed(UserInput userInput) {
        String mainDefinition = userInput.getMainDefinition();
        if (mainDefinition.trim().startsWith("//") || mainDefinition.trim().startsWith("/*")) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.FAILURE,
                    "The main definition should not start with a comment.");
        }
        return new MuroMuroResponse(MuroMuroResponse.Status.SUCCESS, "");
    }

    private String buildEvaluationCode(UserInput userInput) {
        String callerCode = userInput.getCallerCode();
        String mainDefinition = userInput.getMainDefinition();
        mainDefinition = prependStaticIfMissing(mainDefinition);
        return evaluationCode
                .replaceCallerCode(callerCode)
                .replaceMainDefinition(mainDefinition)
                .getFormattedContent();
    }

    private static MuroMuroResponse analyzeEvaluation(String dockerEvalOutput) {
        if (dockerEvalOutput.contains("error: not a statement")
                || dockerEvalOutput.contains("error: ';' expected")
                || dockerEvalOutput.contains("error: <identifier> expected")
                || dockerEvalOutput.contains("Error: Could not find or load main class")) {
            return new MuroMuroResponse(MuroMuroResponse.Status.FAILURE, "Invalid solution.");
        }
        else if (dockerEvalOutput.contains("Killed")) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.TIMEOUT,
                    "The solution took too long to execute.");
        }
        else if (dockerEvalOutput.contains("is incorrectly formed.")) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.FAILURE,
                    "Incorrect solution. One or more of the lists are not correctly populated.");
        }
        else if (areAllListsCorrectlyFormed(dockerEvalOutput)) {
            return new MuroMuroResponse(MuroMuroResponse.Status.SUCCESS);
        }

        return new MuroMuroResponse(
                MuroMuroResponse.Status.UNKNOWN,
                "Internal server failure. Please contact support with the following output:\n"
                        + dockerEvalOutput);
    }

    private static boolean areAllListsCorrectlyFormed(String dockerEvalOutput) {
        for (int i = 1; i <= 4; i++) {
            String neededPhrase = String.format("List %d is correctly formed.", i);
            if (!dockerEvalOutput.contains(neededPhrase)) {
                return false;
            }
        }
        return true;
    }
}
