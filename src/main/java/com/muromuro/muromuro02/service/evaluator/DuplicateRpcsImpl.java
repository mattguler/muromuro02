package com.muromuro.muromuro02.service.evaluator;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import com.muromuro.muromuro02.model.UserInput;
import com.muromuro.muromuro02.service.docker.DockerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import static com.muromuro.muromuro02.service.utils.Utils.buildFailureMessage;

/** The evaluator for the Duplicate RPCs question. */
@Service
public class DuplicateRpcsImpl extends AbstractEvaluatorImpl {

    private static final int USER_CODE_MAX_LENGTH = 5000;

    private final InitialSolution initialSolution;

    @Autowired
    public DuplicateRpcsImpl(
            DockerProxy dockerProxy,
            @Qualifier("duplicateRpcs") EvaluationCode evaluationCode,
            @Qualifier("duplicateRpcsSoln") InitialSolution initialSolution) {
        super(dockerProxy, evaluationCode);
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

    // TODO: Refactor this method here (and everywhere else) to return
    //     an error status value in addition to the actual result.
    @Override
    protected String buildEvaluationCode(UserInput userInput) {
        String callerCode =
                refineCallerCode(userInput.getCallerCode());
        return evaluationCode
                .replaceCallerCode(callerCode)
                .replaceMainDefinition(userInput.getMainDefinition())
                .getFormattedContent();
    }

    @Override
    protected MuroMuroResponse analyzeEvaluation(
            String dockerEvalOutput, UserInput userInput) {
        // TODO: Fully implement this analysis.
        return new MuroMuroResponse(
                MuroMuroResponse.Status.UNKNOWN,
                buildFailureMessage(
                        dockerEvalOutput,
                        "Duplicate RPCs evaluator not yet implemented."));
    }

    /**
     * Makes updates to the given caller code help simulate the test scenarios
     * for duplicating the RPCs.
     */
    private String refineCallerCode(String callerCode) {
        // TODO: Implement the simulated test scenarios for duplicated RPCs.
        //     Also implement some more validation of the caller code input.
        String clientCode1 =
                callerCode.replace("KVListClient", "KVListClient1");
        String clientCode2 =
                callerCode.replace("KVListClient", "KVListClient2");
        String clientCode3 =
                callerCode.replace("KVListClient", "KVListClient3");
        return clientCode1 + "\n\n" + clientCode2 + "\n\n" + clientCode3;
    }
}
