/**
 * The initial solution for the RefactorTooManyIfs question.
 * This is a resource file, not part of the main codebase.
 * It is read by the main MuroMuro server, and then its relevant parts are
 * used by the server.
 */
public class RefactorTooManyIfsSoln {

    // Start initial solution implementation.
    public int calculateSomething(String input) {
        int result = input;
        if (input.equals("a")) {
            result += 1;
        }
        else if (input.equals("b")) {
            result -= 5;
        }
        else if (input.equals("c")) {
            result *= 4;
        }
        else if (input.equals("d")) {
            result *= result;
        }
        else if (input.equals("e")) {
            result %= 10;
        }
        else if (input.equals("f")) {
            result += 7;
        }
        else {
            result = result;
        }
        return result;
    }
    // End initial solution implementation.
}
