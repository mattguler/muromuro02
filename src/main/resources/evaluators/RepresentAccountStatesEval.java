import java.util.*;
import java.util.stream.*;


/**
 * The evaluator for the RepresentAccountStates question.
 * This is a resource file, not part of the main codebase.
 * It is read by the main MuroMuro server, and then its relevant parts are
 * replaced by the user-input solutions. Then the resulting code is sent to the
 * remote Docker container to do an evaluation of the user solution.
 */
public class RepresentAccountStatesEval {

    // Start main definition implementation.
    public enum AccountState {
        INACTIVE,
        ACTIVE,
        SUSPENDED,
        DELETED
    }
    // End main definition implementation.

    public void evalAccountState(AccountState accountState) {
        switch(accountState) {
            case INACTIVE:
                System.out.println("Account state INACTIVE.");
                break;
            case ACTIVE:
                System.out.println("Account state ACTIVE.");
                break;
            case SUSPENDED:
                System.out.println("Account state SUSPENDED.");
                break;
            case DELETED:
                System.out.println("Account state DELETED.");
                break;
            default:
                System.out.println("Invalid account state.");
                break;
        }
    }

    public static void main(String[] args) {
        RepresentAccountStatesEval eval = new RepresentAccountStatesEval();
        eval.evalAccountState(AccountState.INACTIVE);
        eval.evalAccountState(AccountState.ACTIVE);
        eval.evalAccountState(AccountState.SUSPENDED);
        eval.evalAccountState(AccountState.DELETED);
    }
}
