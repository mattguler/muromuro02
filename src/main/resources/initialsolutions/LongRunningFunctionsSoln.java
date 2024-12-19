import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

/**
 * The initial solution for the LongRunningFunctions question.
 * This is a resource file, not part of the main codebase.
 * It is read by the main MuroMuro server, and then its relevant parts are
 * used by the server.
 */
public class LongRunningFunctionsSoln {

    // To help this code compile and not give any errors in the IDE,
    // the following class definitions have been provided.

    public static class Account {
        private int value;
        public Account(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
        public void setValue(int value) {
            this.value = value;
        }
    }

    public static class BlackBox {
        private final int id;
        public BlackBox(int id) {
            this.id = id;
        }
        public boolean process(
                Account account,
                int code,
                BiConsumer<Account, Integer> accountUpdater) {
            // Skipping most of the implementation here.
            int amount = 15;
            accountUpdater.accept(account, amount);
            return true;
        }
    }

    // Start initial main definition implementation.
    // Returns true if no BlackBoxes fail during their process.
    public boolean runAllBlackBoxes(
            List<BlackBox> blackBoxes,
            Account account) {
        for (int code = 0; code < 10; code++) {
            for (BlackBox blackBox : blackBoxes) {
                boolean result =
                    blackBox.process(
                            account,
                            code,
                            (acct, amount) ->
                                    acct.setValue(acct.getValue() + amount));
                if (!result) {
                    return false;
                }
            }
        }
        return true;
    }
    // End initial main definition implementation.
}
