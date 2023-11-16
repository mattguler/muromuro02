/**
 * The initial solution for the RefactorTooManyIfs question.
 * This is a resource file, not part of the main codebase.
 * It is read by the main MuroMuro server, and then its relevant parts are
 * used by the server.
 */
public class RefactorTooManyIfsSoln {

    // Start initial solution implementation.
    public int calculateSomething(String input) {
        int result = 0;
        if (input.equals("a")) {
            result += 1;
        }
        if (input.equals("b")) {
            result += 2;
        }
        return result;
    }
    // End initial solution implementation.
}
