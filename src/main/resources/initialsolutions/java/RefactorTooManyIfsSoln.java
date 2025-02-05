/**
 * The initial solution for the RefactorTooManyIfs question.
 * This is a resource file, not part of the main codebase.
 * It is read by the main MuroMuro server, and then its relevant parts are
 * used by the server.
 */
public class RefactorTooManyIfsSoln {

    // Start initial main definition implementation.
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
    // End initial main definition implementation.
}
