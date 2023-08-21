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
        // TODO: Populate the initial solution here, as necessary.
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
        // TODO: Implement a proper evaluation here.
        return new MuroMuroResponse(
                MuroMuroResponse.Status.UNKNOWN,
                "Evaluation code, for debugging purposes:\n"
                        + evalCode
                        + "\n\nDocker output content, for debugging purposes:\n"
                        + dockerEvalOutput);
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
}
