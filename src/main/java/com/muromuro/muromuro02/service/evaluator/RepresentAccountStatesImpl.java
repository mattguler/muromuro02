package com.muromuro.muromuro02.service.evaluator;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import com.muromuro.muromuro02.service.docker.DockerProxy;
import com.muromuro.muromuro02.service.utils.Security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.muromuro.muromuro02.service.utils.Utils.replaceEnumNames;
import static com.muromuro.muromuro02.service.utils.Utils.replaceTargetWords;

/** The evaluator for the RepresentAccountStates question. */
@Service
public class RepresentAccountStatesImpl implements Evaluator {
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

    private static final String EVALUATOR_PREFIX =
            "public class Evaluator {\n\n";

    private static final String EVALUATOR_SUFFIX =
            "\n\nstatic void evalAccountState(AccountState accountState) {\n"
                    + "  switch(accountState) {\n"
                    + "    case INACTIVE:\n"
                    + "      System.out.println(\\\"Account state INACTIVE.\\\");\n"
                    + "      break;\n"
                    + "    case ACTIVE:\n"
                    + "      System.out.println(\\\"Account state ACTIVE.\\\");\n"
                    + "      break;\n"
                    + "    case SUSPENDED:\n"
                    + "      System.out.println(\\\"Account state SUSPENDED.\\\");\n"
                    + "      break;\n"
                    + "    case DELETED:\n"
                    + "      System.out.println(\\\"Account state DELETED.\\\");\n"
                    + "      break;\n"
                    + "    default:\n"
                    + "      System.out.println(\\\"Invalid account state.\\\");\n"
                    + "      break;\n"
                    + "  }\n"
                    + "}\n\n"
                    + "public static void main(String[] args) {\n"
                    + "  evalAccountState(AccountState.INACTIVE);\n"
                    + "  evalAccountState(AccountState.ACTIVE);\n"
                    + "  evalAccountState(AccountState.SUSPENDED);\n"
                    + "  evalAccountState(AccountState.DELETED);\n"
                    + "}\n"
                    + "}\n"; // This closes out the class definition in the prefix.

    private final DockerProxy dockerProxy;

    @Autowired
    public RepresentAccountStatesImpl(DockerProxy dockerProxy) {
        this.dockerProxy = dockerProxy;
    }

    /**
     * Returns the initial solution for the RepresentAccountStates question.
     * This is the placeholder solution until the user enters their own solution.
     */
    @Override
    public String getInitialSolution() {
        return INITIAL_SOLUTION;
    }

    /**
     * Evaluates the user's solution to the RepresentAccountStates question.
     *
     * @param userInput The user's solution to the RepresentAccountStates question.
     * @return The evaluation response to the user's solution.
     */
    @Override
    public MuroMuroResponse evaluateSolution(String userInput) {
        MuroMuroResponse securityResponse = checkIfCodeSecure(userInput);
        if (securityResponse.getStatus() != MuroMuroResponse.Status.SUCCESS) {
            return securityResponse;
        }
        String evaluationCode = buildEvaluationCode(userInput);
        String containerId = dockerProxy.startContainer(evaluationCode);
        String dockerEvalOutput = dockerProxy.getContainerOutput(containerId);
        // Note: Comment this out when debugging the container logs.
        dockerProxy.cleanUpContainer(containerId);
        return analyzeEvaluation(dockerEvalOutput, userInput);
    }

    private static MuroMuroResponse checkIfCodeSecure(String userInput) {
        return MuroMuroResponse.combineResponses(
                Security.validateCodeLength(userInput, USER_CODE_MAX_LENGTH),
                Security.checkIfCodeSecure(userInput));
    }

    private static String buildEvaluationCode(String userInput) {
        userInput = replaceEnumNames(userInput, ACCOUNT_STATE_ENUM_NAME);
        // Note: The order of these replacements is important. Inactive should come before Active.
        userInput = replaceTargetWords(userInput, ACCOUNT_STATE_INACTIVE, "Inactive", "inactive");
        userInput = replaceTargetWords(userInput, ACCOUNT_STATE_ACTIVE, "Active", "active");
        userInput = replaceTargetWords(userInput, ACCOUNT_STATE_SUSPENDED, "Suspended", "suspended");
        userInput = replaceTargetWords(userInput, ACCOUNT_STATE_DELETED, "Deleted", "deleted");
        return EVALUATOR_PREFIX + userInput + EVALUATOR_SUFFIX;
    }

    private static MuroMuroResponse analyzeEvaluation(String dockerEvalOutput, String userInput) {
        if (containsAllAccountStates(dockerEvalOutput)) {
            return new MuroMuroResponse(MuroMuroResponse.Status.SUCCESS);
        }
        else if (!dockerEvalOutput.contains("error: cannot find symbol")) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.FAILURE,
                    "Invalid definition.");
        }
        StringBuilder errorMessage = new StringBuilder();
        if (!userInput.contains("enum") && userInput.contains("boolean")) {
            errorMessage.append("Incorrect solution. ");
            errorMessage.append(
                    "(Hint: Can you use a better data type than boolean to represent the account states?)");
        }
        else if (!userInput.contains("enum")) {
            errorMessage.append("Incorrect solution. ");
            errorMessage.append(
                    "(Hint: Can you think of a better data type to represent the account states?)");
        }
        else {
            errorMessage.append("The solution does not seem to represent all the necessary account states, ");
            errorMessage.append("which are: ");
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
