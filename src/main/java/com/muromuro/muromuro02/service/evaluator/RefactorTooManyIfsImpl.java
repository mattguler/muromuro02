package com.muromuro.muromuro02.service.evaluator;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import com.muromuro.muromuro02.model.UserInput;
import com.muromuro.muromuro02.service.docker.DockerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import static com.muromuro.muromuro02.service.utils.Security.checkIfCodeSecure;
import static com.muromuro.muromuro02.service.utils.Security.validateCodeLength;
import static com.muromuro.muromuro02.service.utils.Utils.*;

/** The evaluator for the RefactorTooManyIfs question. */
@Service
public class RefactorTooManyIfsImpl implements Evaluator {

    private static final int USER_CODE_MAX_LENGTH = 2500;

    private final InitialSolution initialSolution;
    private final EvaluationCode evaluationCode;
    private final DockerProxy dockerProxy;

    @Autowired
    public RefactorTooManyIfsImpl(
            @Qualifier("refactorTooManyIfsSoln") InitialSolution initialSolution,
            @Qualifier("refactorTooManyIfs") EvaluationCode evaluationCode,
            DockerProxy dockerProxy) {
        this.initialSolution = initialSolution;
        this.evaluationCode = evaluationCode;
        this.dockerProxy = dockerProxy;
    }

    @Override
    public UserInput getInitialSolution() {
        return new UserInput("", initialSolution.getRelevantContent());
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
        return analyzeEvaluation(dockerEvalOutput, userInput);
    }

    // TODO: Also prevent the user from entering “import” statements, here and
    // in the other evaluators.
    private static MuroMuroResponse checkIfCodeSecureAndCorrect(UserInput userInput) {
        return MuroMuroResponse.combineResponses(
                validateCodeLength(userInput, USER_CODE_MAX_LENGTH),
                checkIfCodeSecure(userInput),
                validateNotStartsWithComments(userInput));
    }

    private String buildEvaluationCode(UserInput userInput) {
        String mainDefinition = userInput.getMainDefinition();
        mainDefinition = prependStaticIfMissing(mainDefinition);
        return evaluationCode
                .replaceMainDefinition(mainDefinition)
                .getFormattedContent();
    }

    private static MuroMuroResponse analyzeEvaluation(
            String dockerEvalOutput,
            UserInput userInput) {
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
        else if (dockerEvalOutput.contains("Validation 1 failed.")
                || dockerEvalOutput.contains("Validation 2 failed.")) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.FAILURE,
                    "Incorrect solution. The calculation output is wrong for some inputs.");
        }
        else if (!dockerEvalOutput.contains("Validation 1 passed.")
                || !dockerEvalOutput.contains("Validation 2 passed.")) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.FAILURE,
                    "Incorrect solution.");
        }
        else if (getIfCountInCode(userInput) > 2) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.FAILURE,
                    "While the solution technically works, "
                            + "it still contains too many if statements.");
        }

        return new MuroMuroResponse(
                MuroMuroResponse.Status.SUCCESS,
                "The solution looks correct, but your interviewer "
                        + "will be the final judge.");
    }

    private static int getIfCountInCode(UserInput userInput) {
        String mainDef = userInput.getMainDefinition();
        return countKeywordOccurrences(mainDef, "if(")
                + countKeywordOccurrences(mainDef, "if (");
    }
}
