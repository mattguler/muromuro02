package com.muromuro.muromuro02.service.evaluator;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import com.muromuro.muromuro02.model.UserInput;
import com.muromuro.muromuro02.service.docker.DockerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import static com.muromuro.muromuro02.service.utils.Utils.buildFailureMessage;
import static com.muromuro.muromuro02.service.utils.Utils.countKeywordOccurrences;

/** The evaluator for the RefactorTooManyIfs question. */
@Service
public class RefactorTooManyIfsImpl extends AbstractEvaluatorImpl {

    private static final int USER_CODE_MAX_LENGTH = 2500;

    private final InitialSolution initialSolution;

    @Autowired
    public RefactorTooManyIfsImpl(
            @Qualifier("refactorTooManyIfsSoln") InitialSolution initialSolution,
            @Qualifier("refactorTooManyIfs") EvaluationCode evaluationCode,
            DockerProxy dockerProxy) {
        super(dockerProxy, evaluationCode);
        this.initialSolution = initialSolution;
    }

    @Override
    public UserInput getInitialSolution() {
        return new UserInput("", initialSolution.getRelevantContent());
    }

    @Override
    protected int getUserCodeMaxLength() {
        return USER_CODE_MAX_LENGTH;
    }

    @Override
    protected String buildEvaluationCode(UserInput userInput) {
        return evaluationCode
                .replaceMainDefinition(userInput.getMainDefinition())
                .getFormattedContent();
    }

    @Override
    protected MuroMuroResponse analyzeEvaluation(
            String dockerEvalOutput, UserInput userInput) {
        MuroMuroResponse initialAnalysis = analyzeForBasicErrors(dockerEvalOutput);
        if (initialAnalysis.getStatus() != MuroMuroResponse.Status.SUCCESS) {
            return initialAnalysis;
        }
        if (dockerEvalOutput.contains("Validation 1 failed.")
                || dockerEvalOutput.contains("Validation 2 failed.")) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.FAILURE,
                    buildFailureMessage(
                            dockerEvalOutput,
                            "Incorrect solution. The calculation output is\n"
                                    + "wrong for some inputs."));
        }
        else if (!dockerEvalOutput.contains("Validation 1 passed.")
                || !dockerEvalOutput.contains("Validation 2 passed.")) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.FAILURE,
                    buildFailureMessage(dockerEvalOutput, "Incorrect solution."));
        }
        else if (getIfCountInCode(userInput) > 2) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.FAILURE,
                    buildFailureMessage(
                            dockerEvalOutput,
                            "While the solution technically works,\n"
                                    + "it still contains too many if statements."));
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
