package com.muromuro.muromuro02.service.evaluator;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import com.muromuro.muromuro02.model.UserInput;
import com.muromuro.muromuro02.service.docker.DockerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class DebugListImpl extends AbstractEvaluatorImpl {

    private static final int USER_CODE_MAX_LENGTH = 2000;

    private final InitialSolution initialSolution;

    @Autowired
    public DebugListImpl(
            DockerProxy dockerProxy,
            @Qualifier("debugList") EvaluationCode evaluationCode,
            @Qualifier("debugListSoln") InitialSolution initialSolution) {
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
        String mainDefinition = userInput.getMainDefinition();
        mainDefinition =
                mainDefinition.replace("DebugListSoln", "DebugListEval");
        return evaluationCode
                .replaceMainDefinition(mainDefinition)
                .getFormattedContent();
    }

    @Override
    protected MuroMuroResponse analyzeEvaluation(String dockerEvalOutput, UserInput userInput) {
        // TODO: Fully implement this analysis.
        return new MuroMuroResponse(
                MuroMuroResponse.Status.UNKNOWN,
                String.format(
                        "Debug List evaluator not yet implemented.\n\n%s",
                        dockerEvalOutput));
    }
}
