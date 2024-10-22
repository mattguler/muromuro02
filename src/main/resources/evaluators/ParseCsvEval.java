import java.util.*;
import java.util.stream.*;

/**
 * The evaluator for the ParseCsv question.
 * This is a resource file, not part of the main codebase.
 * It is read by the main MuroMuro server, and then its relevant parts are
 * replaced by the user-input solutions. Then the resulting code is sent to the
 * remote Docker container to do an evaluation of the user solution.
 */
public class ParseCsvEval {

    public static final String CSV_INPUT_00 =
            """
                    id, account_name, account_state, account_age, owner_first_name, owner_last_name
                    """;

    public static final String CSV_INPUT_01 =
            """
                    id, account_name, account_state, account_age, owner_first_name, owner_last_name
                    15005, Rocinante, INACTIVE, 4, Joe, Miller
                    """;

    public static final String CSV_INPUT_02 =
            """
                    id, account_name, account_state, account_age, owner_first_name, owner_last_name
                    15001, Foobar, , 5, Matt, Smith
                    15002, Barbaz, ACTIVE, 2, , Brown
                    15005, Rocinante, INACTIVE, 4, Joe, Miller
                    15007, , UNDEFINED, 17, Jean Luc, Picard
                    , Earth, INACTIVE, 15, Will, Riker
                    15015, Vulcan, ACTIVE, 5, Spock,
                    15025, Enterprise, ACTIVE, , James, Kirk
                    """;

    public static final String CSV_INPUT_03 =
            """
                    id, account_name, account_state, account_age, owner_first_name, owner_last_name
                    , , , , ,
                    , , , , ,
                    15015, Vulcan, ACTIVE, 5, Spock,
                    15025, Enterprise, ACTIVE, , James, Kirk
                    """;

    public enum AccountState {
        UNDEFINED,
        ACTIVE,
        INACTIVE
    }

    public record Account(
            int id,
            String name,
            AccountState accountState,
            int accountAge,
            String ownerFirstName,
            String ownerLastName
    ) {}

    // Start main definition implementation.
    public List<Account> parseCsv(String csv) {
        return List.of();
    }
    // End main definition implementation.

    public void printAccounts(List<Account> accounts) {
        for (Account account : accounts) {
            System.out.println(account);
        }
    }

    public void runTest0() {
        List<Account> expected = List.of();
        List<Account> accounts = parseCsv(CSV_INPUT_00);
        if (!assertEquals(expected, accounts, /* testId= */ 0)) {
            return;
        }
        System.out.println("Test 0 passed.");
    }

    public void runTest1() {
        List<Account> expected =
                List.of(
                        new Account(
                                15005,
                                "Rocinante",
                                AccountState.INACTIVE,
                                4,
                                "Joe",
                                "Miller"));
        List<Account> accounts = parseCsv(CSV_INPUT_01);
        if (!assertEquals(expected, accounts, /* testId= */ 1)) {
            return;
        }
        System.out.println("Test 1 passed.");
    }

    public void runTest2() {
        List<Account> expected =
                List.of(
                        new Account(
                                15001,
                                "Foobar",
                                AccountState.UNDEFINED,
                                5,
                                "Matt",
                                "Smith"),
                        new Account(
                                15002,
                                "Barbaz",
                                AccountState.ACTIVE,
                                2,
                                "",
                                "Brown"),
                        new Account(
                                15005,
                                "Rocinante",
                                AccountState.INACTIVE,
                                4,
                                "Joe",
                                "Miller"),
                        new Account(
                                15007,
                                "",
                                AccountState.UNDEFINED,
                                17,
                                "Jean Luc",
                                "Picard"),
                        new Account(
                                0,
                                "Earth",
                                AccountState.INACTIVE,
                                15,
                                "Will",
                                "Riker"),
                        new Account(
                                15015,
                                "Vulcan",
                                AccountState.ACTIVE,
                                5,
                                "Spock",
                                ""),
                        new Account(
                                15025,
                                "Enterprise",
                                AccountState.ACTIVE,
                                0,
                                "James",
                                "Kirk"));
        List<Account> accounts = parseCsv(CSV_INPUT_02);
        if (!assertEquals(expected, accounts, /* testId= */ 2)) {
            return;
        }
        System.out.println("Test 2 passed.");
    }

    public void runTest3() {
        List<Account> expected =
                List.of(
                        new Account(0, "", AccountState.UNDEFINED, 0, "", ""),
                        new Account(0, "", AccountState.UNDEFINED, 0, "", ""),
                        new Account(
                                15015,
                                "Vulcan",
                                AccountState.ACTIVE,
                                5,
                                "Spock",
                                ""),
                        new Account(
                                15025,
                                "Enterprise",
                                AccountState.ACTIVE,
                                0,
                                "James",
                                "Kirk"));
        List<Account> accounts = parseCsv(CSV_INPUT_03);
        if (!assertEquals(expected, accounts, /* testId= */ 3)) {
            return;
        }
        System.out.println("Test 3 passed.");
    }

    private boolean assertEquals(
            List<Account> expected, List<Account> result, int testId) {
        if (expected.size() != result.size()) {
            System.out.println(
                    "Test " + testId + " failed. "
                            + "Parse function should have returned "
                            + expected.size() + " accounts, not "
                            + result.size() + ".");
            return false;
        }
        for (int i = 0; i < expected.size(); i++) {
            if (!assertEquals(expected.get(i), result.get(i), testId, i)) {
                return false;
            }
        }
        return true;
    }

    private boolean assertEquals(
            Account expected, Account result, int testId, int accountNum) {
        if (result.id() != expected.id() ||
                !result.name().equals(expected.name()) ||
                result.accountState() != expected.accountState() ||
                result.accountAge() != expected.accountAge() ||
                !result.ownerFirstName().equals(expected.ownerFirstName()) ||
                !result.ownerLastName().equals(expected.ownerLastName())) {
            System.out.println(
                    "Test " + testId + " failed. "
                            + "Account " + accountNum + " in the result list "
                            + "doesn't match the expected.");
            System.out.println("***** Expected:");
            System.out.println(expected);
            System.out.println("***** Actual");
            System.out.println(result);
            System.out.println("*****");
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        ParseCsvEval parseCsvEval = new ParseCsvEval();
        parseCsvEval.runTest0();
        parseCsvEval.runTest1();
        parseCsvEval.runTest2();
        parseCsvEval.runTest3();
    }
}
