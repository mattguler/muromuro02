/**
 * The initial solution for the Incompatible Interfaces question.
 * This is a resource file, not part of the main codebase.
 * It is read by the main Muromuro server, and then its relevant parts are
 * used by the server.
 */
public class IncompatibleInterfacesSoln {

    public interface Processor {
        int run(int x, int y);
    }

    public static final class Processor1 implements Processor {
        @Override
        public int run(int x, int y) {
            return 0;
        }
    }

    public static final class Processor2 {
        public int makeCalculations(int x, int y) {
            return 0;
        }
    }

    public static final class Processor3 {

        public int execute(int x, int y) {
            return 0;
        }
    }

    public static final class Client {
        private final int y;
        public Client(int y) {
            this.y = y;
        }
        public int runProcess(Processor processor, int x) {
            return processor.run(x, y);
        }
    }

// Altered its indendation slightly to make it look better
// on the question's page view.
public static final class MyClass {
    // Start initial solution implementation.
    // Feel free to alter these fields or add new fields here.
    private final Client client;
    private final Processor processor1;
    private final Processor2 processor2;
    private final Processor3 processor3;

    // You cannot alter the constructor signature, but you can
    // change its implementation in any way you want.
    public MyClass(
            Client client,
            Processor processor1,
            Processor2 processor2,
            Processor3 processor3) {
        this.client = client;
        this.processor1 = processor1;
        this.processor2 = processor2;
        this.processor3 = processor3;
    }

    public int runProcess(int x) {
        // Re-implement this without using the if-statements,
        // if you can.
        if (x == 2 || x == 3) {
            // Make this work please. Also, there should be
            // an offset of -1 applied to Processor2's y somehow.
            return client.runProcess(processor2, x);
        }
        else if (x == 1 || x == 4) {
            // Make this work please. Also, there should be
            // an offset of +1 applied to Processor3's y somehow.
            return client.runProcess(processor3, x);
        }
        else {
            return client.runProcess(processor1, x);
        }
    }

    // Feel free to add any additional helper methods, data structures,
    // or static inner classes to this code.
    // End initial solution implementation.

    // Other methods and data structures might already be defined here.
}
}
