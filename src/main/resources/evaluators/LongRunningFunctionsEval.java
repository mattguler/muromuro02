import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

/**
 * The evaluator for the LongRunningFunctions question.
 * This is a resource file, not part of the main codebase.
 * It is read by the main MuroMuro server, and then its relevant parts are
 * replaced by the user-input solutions. Then the resulting code is sent to the
 * remote Docker container to do an evaluation of the user solution.
 */
public class LongRunningFunctionsEval {

    private static final long PROCESS_SLEEP_TIME_IN_MS = 200;
    private static final long EXPECTED_RUN_TIME_IN_MS = 400;

    private static final int ACCOUNT_START_VALUE = 10000;
    private static final int ACCOUNT_EXPECTED_END_VALUE = 10050;

    private static final int[] RANDOM_NUMS =
            {
                    -28, 42, -22, -34, 41, -49, 24, 30, 2, -21,
                    -16, -2, -5, -48, -11, 38, 9, -47, 39, -44,
                    25, 11, 33, 20, 49, 13, -33, -3, 15, 7,
                    8, -24, -23, -42, -40, -31, 22, -8, -29, 28,
                    -14, -30, -32, 26, -19, 4, -38, 0, 50, 19,
                    -46, 44, -7, -45, 45, 43, 40, -43, -39, 16,
                    31, 5, 23, 34, -17, -25, 14, -13, 29, 36,
                    -26, 10, 17, -18, 48, 32, -35, 1, -41, 46,
                    -36, 12, -15, -6, -12, -27, 3, 21, 37, -20,
                    -37, -9, 18, 27, -4, 35, -10, 47, -1, 6
            };

    // The class definition for the shared Account object.
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

    // The Blackbox processor that determines the Account update amount.
    public static class BlackBox {

        // The unique ID for each BlackBox object.
        private final int id;

        // Determines whether some of the process() calls will fail.
        private final boolean canFail;

        public BlackBox(int id) {
            this(id, false);
        }

        public BlackBox(int id, boolean canFail) {
            this.id = id;
            this.canFail = canFail;
        }

        // Returns true if the account is successfully processed, false otherwise.
        public boolean process(
                Account account,
                int code,
                BiConsumer<Account, Integer> accountUpdater) {
            try {
                Thread.sleep(PROCESS_SLEEP_TIME_IN_MS);
            }
            catch (InterruptedException e) {
                System.err.println(e);
                return false;
            }
            int index = id * 10 + code;
            int amount = RANDOM_NUMS[index];
            if (canFail && amount <= 0) {
                return false;
            }
            accountUpdater.accept(account, amount);
            return true;
        }
    }

    // Start main definition implementation.
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
    // End main definition implementation.

    // This is used to time program execution.
    static class Timer {
        private final long startTime;
        private Timer() {
            this.startTime = System.currentTimeMillis();
        }
        static Timer start() {
            return new Timer();
        }
        long getElapsedTime() {
            final long stopTime = System.currentTimeMillis();
            return stopTime - startTime;
        }
    }

    List<BlackBox> createBlackBoxes() {
        List<BlackBox> blackBoxes = new ArrayList<>();
        for (int id = 0; id < 10; id++) {
            blackBoxes.add(new BlackBox(id));
        }
        return blackBoxes;
    }

    List<BlackBox> createFaultyBlackBoxes() {
        List<BlackBox> blackBoxes = new ArrayList<>();
        for (int id = 0; id < 10; id++) {
            if (id == 6) {
                blackBoxes.add(new BlackBox(id, /* canFail= */ true));
            } else {
                blackBoxes.add(new BlackBox(id));
            }
        }
        return blackBoxes;
    }

    public void runTest0() {
        Account account = new Account(ACCOUNT_START_VALUE);
        List<BlackBox> blackBoxes = new ArrayList<>();
        Timer timer = Timer.start();
        boolean result = runAllBlackBoxes(blackBoxes, account);
        long elapsedTime = timer.getElapsedTime();
        if (!result) {
            System.out.println(
                    "Test 0 failed. Running empty list of blackboxes should return true.");
            return;
        }
        if (account.getValue() != ACCOUNT_START_VALUE) {
            System.out.println("Test 0 failed. Unexpected account value.");
            return;
        }
        if (elapsedTime > EXPECTED_RUN_TIME_IN_MS) {
            System.out.println("Test 0 failed. Code took too long to run.");
            return;
        }
        System.out.println("Test 0 passed.");
    }

    public void runTest1() {
        for (int cycle = 0; cycle < 5; cycle++) {
            Account account = new Account(ACCOUNT_START_VALUE);
            List<BlackBox> blackBoxes = createBlackBoxes();
            Timer timer = Timer.start();
            boolean result = runAllBlackBoxes(blackBoxes, account);
            long elapsedTime = timer.getElapsedTime();
            if (!result) {
                System.out.println(
                        "Test 1 failed. Running blackboxes should return true here.");
                return;
            }
            if (account.getValue() != ACCOUNT_EXPECTED_END_VALUE) {
                System.out.println("Test 1 failed. Unexpected account value.");
                return;
            }
            if (elapsedTime > EXPECTED_RUN_TIME_IN_MS) {
                System.out.println("Test 1 failed. Code took too long to run.");
                return;
            }
        }
        System.out.println("Test 1 passed.");
    }

    public void runTest2() {
        for (int cycle = 0; cycle < 5; cycle++) {
            Account account = new Account(ACCOUNT_START_VALUE);
            List<BlackBox> blackBoxes = createFaultyBlackBoxes();
            Timer timer = Timer.start();
            boolean result = runAllBlackBoxes(blackBoxes, account);
            long elapsedTime = timer.getElapsedTime();
            if (result) {
                System.out.println(
                        "Test 2 failed. Running blackboxes should return false here.");
                return;
            }
            if (elapsedTime > EXPECTED_RUN_TIME_IN_MS) {
                System.out.println("Test 2 failed. Code took too long to run.");
                return;
            }
        }
        System.out.println("Test 2 passed.");
    }

    public static void main(String[] args) {
        LongRunningFunctionsEval eval = new LongRunningFunctionsEval();
        System.out.println("Running test 0.");
        eval.runTest0();
        System.out.println("Running test 1.");
        eval.runTest1();
        System.out.println("Running test 2.");
        eval.runTest2();
    }
}
