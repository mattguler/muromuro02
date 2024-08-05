package com.muromuro.muromuro02.service.evaluator;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import com.muromuro.muromuro02.model.UserInput;
import com.muromuro.muromuro02.service.docker.DockerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import static com.muromuro.muromuro02.service.utils.Utils.buildFailureMessage;

/** The evaluator for the DesignApiWithPagination question. */
@Service
public class DesignApiWithPaginationImpl extends AbstractEvaluatorImpl {
    private static final int USER_CODE_MAX_LENGTH = 2500;

    @Autowired
    public DesignApiWithPaginationImpl(
            DockerProxy dockerProxy,
            @Qualifier("designApiWithPagination") EvaluationCode evaluationCode) {
        super(dockerProxy, evaluationCode);
    }

    @Override
    protected int getUserCodeMaxLength() {
        return USER_CODE_MAX_LENGTH;
    }

    @Override
    protected String buildEvaluationCode(UserInput userInput) {
        return evaluationCode
                .replaceCallerCode(userInput.getCallerCode())
                .replaceMainDefinition(userInput.getMainDefinition())
                .getFormattedContent();
    }

    @Override
    protected MuroMuroResponse analyzeEvaluation(
            String dockerEvalOutput, UserInput unusedUserInput) {
        MuroMuroResponse initialAnalysis = analyzeForBasicErrors(dockerEvalOutput);
        if (initialAnalysis.getStatus() != MuroMuroResponse.Status.SUCCESS) {
            return initialAnalysis;
        }
        if (dockerEvalOutput.contains("is incorrectly formed.")) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.FAILURE,
                    buildFailureMessage(
                            dockerEvalOutput,
                            "Incorrect solution. One or more of the lists are not\n"
                                    + "correctly populated."));
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
