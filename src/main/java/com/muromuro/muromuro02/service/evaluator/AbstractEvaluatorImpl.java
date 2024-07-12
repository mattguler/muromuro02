package com.muromuro.muromuro02.service.evaluator;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import com.muromuro.muromuro02.model.UserInput;
import com.muromuro.muromuro02.service.docker.DockerProxy;

import static com.muromuro.muromuro02.service.utils.Security.checkIfCodeSecure;
import static com.muromuro.muromuro02.service.utils.Security.validateCodeLength;
import static com.muromuro.muromuro02.service.utils.Utils.validateNotStartsWithImports;

/**
 * The abstract evaluator implementation which is used to evaluate the MuroMuro solutions.
 * This class is here to decrease the amount of duplicate code in the evaluator implementations.
 */
abstract class AbstractEvaluatorImpl implements Evaluator {

    private final DockerProxy dockerProxy;
    protected final EvaluationCode evaluationCode;

    public AbstractEvaluatorImpl(DockerProxy dockerProxy, EvaluationCode evaluationCode) {
        this.dockerProxy = dockerProxy;
        this.evaluationCode = evaluationCode;
    }

    /**
     * Returns the initial solution for the Muromuro interview question.
     * This is the placeholder solution until the user enters their own solution.
     */
    @Override
    public UserInput getInitialSolution() {
        return new UserInput("", "");
    }

    /**
     * Evaluates the user's solution to the Muromuro interview question.
     * @param userInput The user's solution to the MuroMuro interview question.
     * @return The evaluation response to the user's solution.
     */
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

    /** Returns the maximum length allowed for the user's code. */
    abstract protected int getUserCodeMaxLength();

    /**
     * Checks if the given user's code is secure and correctly formed,
     * before it is sent to an external resource (e.g. Docker) for evaluation.
     */
    protected MuroMuroResponse checkIfCodeSecureAndCorrect(UserInput userInput) {
        return MuroMuroResponse.combineResponses(
                validateCodeLength(userInput, getUserCodeMaxLength()),
                checkIfCodeSecure(userInput),
                validateNotStartsWithImports(userInput.getMainDefinition()));
    }

    /**
     * Builds the code that will be used to evaluate the user's solution.
     * This code will be used by the external resource (e.g. Docker) in the evaluation process.
     */
    abstract protected String buildEvaluationCode(UserInput userInput);

    /**
     * Analyzes the evaluation output from the external resource (e.g. Docker)
     * @param dockerEvalOutput The output from the evaluation process.
     * @param userInput The user's solution to the MuroMuro question.
     * @return The evaluation response to the user's solution.
     */
    abstract protected MuroMuroResponse analyzeEvaluation(
            String dockerEvalOutput, UserInput userInput);
}
