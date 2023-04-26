package com.muromuro.muromuro02.service.evaluator;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import com.muromuro.muromuro02.service.utils.Utils;
import org.springframework.stereotype.Service;

@Service
public class RepresentAccountStatesImpl implements Evaluator {
    private static final String ACCOUNT_STATE_ENUM_NAME = "AccountState";

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

    @Override
    public String getInitialSolution() {
        return INITIAL_SOLUTION;
    }

    @Override
    public String buildEvaluationCode(String userInput) {
        userInput = Utils.replaceEnumNames(userInput, ACCOUNT_STATE_ENUM_NAME);
        return EVALUATOR_PREFIX + userInput + EVALUATOR_SUFFIX;
    }

    @Override
    public MuroMuroResponse analyzeEvaluation(String dockerEvalOutput) {
        if (dockerEvalOutput.contains("Account state INACTIVE")
                && dockerEvalOutput.contains("Account state ACTIVE")
                && dockerEvalOutput.contains("Account state SUSPENDED")
                && dockerEvalOutput.contains("Account state DELETED")) {
            return new MuroMuroResponse(MuroMuroResponse.Status.SUCCESS);
        }
        else if (dockerEvalOutput.contains("error: cannot find symbol")) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.FAILURE,
                    "The solution does not seem to represent all the necessary account states.");
        }
        else {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.FAILURE,
                    "The solution failed with error:\n"
                            + dockerEvalOutput);
        }
    }
}
