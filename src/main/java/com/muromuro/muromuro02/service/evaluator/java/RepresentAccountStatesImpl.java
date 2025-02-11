package com.muromuro.muromuro02.service.evaluator.java;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import com.muromuro.muromuro02.model.UserInput;
import com.muromuro.muromuro02.service.docker.DockerProxy;
import com.muromuro.muromuro02.service.resourcemgmt.JavaEvaluationCode;
import com.muromuro.muromuro02.service.utils.Security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.muromuro.muromuro02.service.utils.Utils.*;

/** The evaluator for the RepresentAccountStates question. */
@Service
public class RepresentAccountStatesImpl extends AbstractJavaEvaluatorImpl {
    private static final int USER_CODE_MAX_LENGTH = 1000;

    private static final String ACCOUNT_STATE_ENUM_NAME = "AccountState";
    private static final String ACCOUNT_STATE_ACTIVE = "ACTIVE";
    private static final String ACCOUNT_STATE_INACTIVE = "INACTIVE";
    private static final String ACCOUNT_STATE_SUSPENDED = "SUSPENDED";
    private static final String ACCOUNT_STATE_DELETED = "DELETED";
    private static final List<String> ACCOUNT_STATES =
            List.of(
                    ACCOUNT_STATE_ACTIVE,
                    ACCOUNT_STATE_INACTIVE,
                    ACCOUNT_STATE_SUSPENDED,
                    ACCOUNT_STATE_DELETED);

    private static final String INITIAL_SOLUTION =
            "boolean isActive = false;";

    @Autowired
    public RepresentAccountStatesImpl(
            DockerProxy dockerProxy,
            @Qualifier("representAccountStates") JavaEvaluationCode evaluationCode,
            @Qualifier("javaSecurityImpl") Security security) {
        super(dockerProxy, evaluationCode, security);
    }

    /**
     * Returns the initial solution for the RepresentAccountStates question.
     * This is the placeholder solution until the user enters their own solution.
     */
    @Override
    public UserInput getInitialSolution() {
        return new UserInput("", INITIAL_SOLUTION);
    }

    @Override
    protected int getUserCodeMaxLength() {
        return USER_CODE_MAX_LENGTH;
    }

    @Override
    protected JavaEvaluationCode buildEvaluationCode(UserInput userInput) {
        String mainDefinition = userInput.getMainDefinition();
        mainDefinition = replaceEnumNames(mainDefinition, ACCOUNT_STATE_ENUM_NAME);
        // Note: The order of these replacements is important. Inactive should come before Active.
        mainDefinition =
                replaceTargetWords(mainDefinition, ACCOUNT_STATE_INACTIVE, "Inactive", "inactive");
        mainDefinition =
                replaceTargetWords(mainDefinition, ACCOUNT_STATE_ACTIVE, "Active", "active");
        mainDefinition =
                replaceTargetWords(mainDefinition, ACCOUNT_STATE_SUSPENDED, "Suspended", "suspended");
        mainDefinition =
                replaceTargetWords(mainDefinition, ACCOUNT_STATE_DELETED, "Deleted", "deleted");
        return evaluationCode
                .replaceMainDefinition(mainDefinition);
    }

    @Override
    protected MuroMuroResponse analyzeEvaluation(
            String dockerEvalOutput, UserInput userInput) {
        if (containsAllAccountStates(dockerEvalOutput)) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.SUCCESS, buildSuccessMessage());
        }
        else if (dockerEvalOutput.contains("Killed")
                && dockerEvalOutput.contains("timeout -s SIGKILL")) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.TIMEOUT,
                    "The solution took too long to execute.");
        }
        else if (!dockerEvalOutput.contains("error: cannot find symbol")) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.FAILURE,
                    "Invalid definition.");
        }
        StringBuilder errorMessage = new StringBuilder();
        String mainDefinition = userInput.getMainDefinition();
        if (!mainDefinition.contains("enum") && mainDefinition.contains("boolean")) {
            errorMessage.append("Incorrect solution.\n");
            errorMessage.append("(Hint: Can you use a better data type than boolean\n");
            errorMessage.append("to represent the account states?)");
        }
        else if (!mainDefinition.contains("enum")) {
            errorMessage.append("Incorrect solution.\n");
            errorMessage.append("(Hint: Can you think of a better data type\n");
            errorMessage.append("to represent the account states?)");
        }
        else {
            errorMessage.append("The solution does not seem to represent\n");
            errorMessage.append("all the necessary account states, which are:\n");
            errorMessage.append(String.join(", ", ACCOUNT_STATES));
        }
        return new MuroMuroResponse(
                MuroMuroResponse.Status.FAILURE, errorMessage.toString());
    }

    private static boolean containsAllAccountStates(String dockerEvalOutput) {
        for (String accountState : ACCOUNT_STATES) {
            String accountStateStr = String.format("Account state %s", accountState);
            if (!dockerEvalOutput.contains(accountStateStr)) {
                return false;
            }
        }
        return true;
    }
}
