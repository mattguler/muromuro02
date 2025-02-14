package com.muromuro.muromuro02.service.evaluator.java;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import com.muromuro.muromuro02.model.UserInput;
import com.muromuro.muromuro02.service.docker.DockerProxy;
import com.muromuro.muromuro02.service.resourcemgmt.JavaEvaluationCode;
import com.muromuro.muromuro02.service.utils.CodeUtils;
import com.muromuro.muromuro02.service.utils.Security;

import static com.muromuro.muromuro02.service.utils.ValidationUtils.buildFailureMessage;

/**
 * The abstract evaluator implementation which is used to evaluate the Java MuroMuro solutions.
 * This class is here to decrease the amount of duplicate code in the evaluator implementations.
 * TODO: Maybe have versions of this class targeting other programming languages as well.
 *       Could also have another abstract base class that contains code common for all languages.
 */
abstract class AbstractJavaEvaluatorImpl implements JavaEvaluator {

    private final DockerProxy dockerProxy;
    // TODO: Maybe define a common EvaluationCode interface for this.
    protected final JavaEvaluationCode evaluationCode;
    protected final CodeUtils codeUtils;
    protected final Security security;

    public AbstractJavaEvaluatorImpl(
            DockerProxy dockerProxy, 
            JavaEvaluationCode evaluationCode,
            CodeUtils codeUtils,
            Security security) {
        this.dockerProxy = dockerProxy;
        this.evaluationCode = evaluationCode;
        this.codeUtils = codeUtils;
        this.security = security;
    }

    /**
     * Returns the initial solution for the Muromuro interview question.
     * This is the placeholder solution until the user enters their own solution.
     */
    @Override
    public abstract UserInput getInitialSolution();

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
        JavaEvaluationCode updatedEvalCode = buildEvaluationCode(userInput);
        if (updatedEvalCode.getBuildResponse().getStatus()
                != MuroMuroResponse.Status.SUCCESS) {
            return updatedEvalCode.getBuildResponse();
        }
        String containerId =
                dockerProxy.startContainer(updatedEvalCode.getFormattedContent());
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
                security.validateCodeLength(userInput, getUserCodeMaxLength()),
                security.checkIfCodeSecure(userInput),
                codeUtils.validateNotStartsWithImports(userInput.getMainDefinition()));
    }

    /**
     * Builds the code that will be used to evaluate the user's solution.
     * This code will be used by the external resource (e.g. Docker) in the evaluation process.
     */
    abstract protected JavaEvaluationCode buildEvaluationCode(UserInput userInput);

    /**
     * Analyzes the evaluation output from the external resource (e.g. Docker)
     * @param dockerEvalOutput The output from the evaluation process.
     * @param userInput The user's solution to the MuroMuro question.
     * @return The evaluation response to the user's solution.
     */
    abstract protected MuroMuroResponse analyzeEvaluation(
            String dockerEvalOutput, UserInput userInput);

    /**
     * Analyzes the evaluation output for basic errors like syntax errors or timeouts.
     * Returns an error response with a status message if an error is detected.
     * Returns a success response if no error is detected.
     */
    protected MuroMuroResponse analyzeForBasicErrors(String dockerEvalOutput) {
        if (dockerEvalOutput.contains("error: not a statement")
                || dockerEvalOutput.contains("error: ';' expected")
                || dockerEvalOutput.contains("error: <identifier> expected")
                || dockerEvalOutput.contains("Error: Could not find or load main class")
                || dockerEvalOutput.contains("Exception in thread \"main\"")) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.FAILURE,
                    buildFailureMessage(
                            dockerEvalOutput,
                            "Invalid solution."));
        }
        else if (dockerEvalOutput.contains("Killed")
                && dockerEvalOutput.contains("timeout -s SIGKILL")) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.TIMEOUT,
                    "The solution took too long to execute.");
        }
        else {
            return new MuroMuroResponse(MuroMuroResponse.Status.SUCCESS);
        }
    }
}
