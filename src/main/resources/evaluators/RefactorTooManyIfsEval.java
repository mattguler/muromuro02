import java.util.*;
import java.util.stream.*;


/**
 * The evaluator for the RefactorTooManyIfs question.
 * This is a resource file, not part of the main codebase.
 * It is read by the main MuroMuro server, and then its relevant parts are
 * replaced by the user-input solutions. Then the resulting code is sent to the
 * remote Docker container to do an evaluation of the user solution.
 */
public class RefactorTooManyIfsEval {

    // Start main definition implementation.

    public int doCalculation(String strInput, int intInput) {
        int result = intInput;
        if (strInput.equals("a")) {
            result += 1;
        }
        else if (strInput.equals("b")) {
            result -= 5;
        }
        else if (strInput.equals("c")) {
            result *= 4;
        }
        else if (strInput.equals("d")) {
            result *= result;
        }
        else if (strInput.equals("e")) {
            result %= 10;
        }
        else if (strInput.equals("f")) {
            result += 7;
        }
        else {
            result = result;
        }
        return result;
    }
    // End main definition implementation.

    public void doValidation1() {
        int intInput = 15;
        if (validateCalculation("a", intInput, 16) &&
                validateCalculation("b", intInput, 10) &&
                validateCalculation("c", intInput, 60) &&
                validateCalculation("d", intInput, 225) &&
                validateCalculation("e", intInput, 5) &&
                validateCalculation("f", intInput, 22) &&
                validateCalculation("g", intInput, 15) &&
                validateCalculation("z", intInput, 15)) {
            System.out.println("Validation 1 passed.");
        }
        else {
            System.out.println("Validation 1 failed.");
        }
    }

    public void doValidation2() {
        int intInput = -22;
        if (validateCalculation("a", intInput, -21) &&
                validateCalculation("b", intInput, -27) &&
                validateCalculation("c", intInput, -88) &&
                validateCalculation("d", intInput, 484) &&
                validateCalculation("e", intInput, -2, 8) &&
                validateCalculation("f", intInput, -15) &&
                validateCalculation("g", intInput, -22) &&
                validateCalculation("z", intInput, -22)) {
            System.out.println("Validation 2 passed.");
        }
        else {
            System.out.println("Validation 2 failed.");
        }
    }

    private boolean validateCalculation(String strInput, int intInput, int expectedOutput) {
        int result = doCalculation(strInput, intInput);
        return result == expectedOutput;
    }

    private boolean validateCalculation(
            String strInput, int intInput, int expectedOutput1, int expectedOutput2) {
        int result = doCalculation(strInput, intInput);
        return result == expectedOutput1 || result == expectedOutput2;
    }

    public static void main(String[] args) {
        RefactorTooManyIfsEval eval = new RefactorTooManyIfsEval();
        eval.doValidation1();
        eval.doValidation2();
    }
}
