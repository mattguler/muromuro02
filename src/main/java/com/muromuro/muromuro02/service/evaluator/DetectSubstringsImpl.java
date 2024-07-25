package com.muromuro.muromuro02.service.evaluator;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import com.muromuro.muromuro02.model.UserInput;
import com.muromuro.muromuro02.service.docker.DockerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/** The evaluator for the Detect Substrings question. */
@Service
public class DetectSubstringsImpl extends AbstractEvaluatorImpl {

    private static final int USER_CODE_MAX_LENGTH = 2000;

    private static final String INITIAL_SOLUTION =
            """
                    public boolean containsFoobar(String input) {
                        // Your implementation goes here.
                    }
                    """;

    @Autowired
    public DetectSubstringsImpl(
            DockerProxy dockerProxy,
            @Qualifier("detectSubstrings") EvaluationCode evaluationCode) {
        super(dockerProxy, evaluationCode);
    }

    @Override
    public UserInput getInitialSolution() {
        return new UserInput("", INITIAL_SOLUTION);
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
    protected MuroMuroResponse analyzeEvaluation(String dockerEvalOutput, UserInput userInput) {
        // TODO: Fully implement this analysis.
        return new MuroMuroResponse(
                MuroMuroResponse.Status.UNKNOWN,
                String.format(
                        "Detect Substrings evaluator not yet implemented.\n\n%s",
                        dockerEvalOutput));
    }
}
