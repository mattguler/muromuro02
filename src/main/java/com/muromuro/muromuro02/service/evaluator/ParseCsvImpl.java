package com.muromuro.muromuro02.service.evaluator;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import com.muromuro.muromuro02.model.UserInput;
import com.muromuro.muromuro02.service.docker.DockerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/** The evaluator for the Parse CSV question. */
@Service
public class ParseCsvImpl extends AbstractEvaluatorImpl {

    private static final int USER_CODE_MAX_LENGTH = 4000;

    private static final String INITIAL_SOLUTION =
            """
                    public List<Account> parseCsv(String csv) {
                        // Your implementation goes here.
                    }
                    """;

    @Autowired
    public ParseCsvImpl(
            DockerProxy dockerProxy,
            @Qualifier("parseCsv") EvaluationCode evaluationCode) {
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
    protected MuroMuroResponse analyzeEvaluation(
            String dockerEvalOutput, UserInput userInput) {
        // TODO: Fully implement this analysis.
        return new MuroMuroResponse(
                MuroMuroResponse.Status.UNKNOWN,
                String.format(
                        "Parse CSV evaluator not yet implemented.\n\n%s",
                        dockerEvalOutput));
    }
}
