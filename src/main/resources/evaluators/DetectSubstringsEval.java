
/**
 * The evaluator for the DetectSubstrings question.
 * This is a resource file, not part of the main codebase.
 * It is read by the main MuroMuro server, and then its relevant parts are
 * replaced by the user-input solutions. Then the resulting code is sent to the
 * remote Docker container to do an evaluation of the user solution.
 */
public class DetectSubstringsEval {

    // Start main definition implementation.
    public boolean containsFoobar(String input) {
        return false;
    }
    // End main definition implementation.

    public void runTest0() {
        String empty = "";
        boolean result = containsFoobar(empty);
        if (!result) {
            System.out.println("Test 0 passed.");
        } else {
            System.out.println("Test 0 failed.");
        }
    }

    public void runTest1() {
        String input =
                """
                        Hello world.
                        This is somefoobarstuff.
                        Goodbye.
                        """;
        boolean result = containsFoobar(input);
        if (result) {
            System.out.println("Test 1 passed.");
        } else {
            System.out.println("Test 1 failed.");
        }
    }

    public void runTest2() {
        String input =
                """
                        Hello world.
                        Thisfoobarbazbat
                        is somefoobarabcdeand
                        foobarxyzwtas well.
                        Goodbye.
                        """;
        boolean result = containsFoobar(input);
        if (!result) {
            System.out.println("Test 2 passed.");
        } else {
            System.out.println("Test 2 failed.");
        }
    }

    public void runTest3() {
        String input =
                """
                        Hello world.
                        Thisfoobarbazbat
                        andfoobarincluded
                        are somefoobarabcdeand
                        foobarxyzwtas well.
                        Goodbye.
                        """;
        boolean result = containsFoobar(input);
        if (result) {
            System.out.println("Test 3 passed.");
        } else {
            System.out.println("Test 3 failed.");
        }
    }

    public void runTest4() {
        String input =
                """
                        Hello world.
                        Contains none of the above.
                        Goodbye.
                        """;
        boolean result = containsFoobar(input);
        if (!result) {
            System.out.println("Test 4 passed.");
        } else {
            System.out.println("Test 4 failed.");
        }
    }

    public static void main(String[] args) {
        DetectSubstringsEval eval = new DetectSubstringsEval();
        eval.runTest0();
        eval.runTest1();
        eval.runTest2();
        eval.runTest3();
        eval.runTest4();
    }
}
