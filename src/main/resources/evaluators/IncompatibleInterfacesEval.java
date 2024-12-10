import java.util.*;
import java.util.function.*;

/**
 * The evaluator for the Incompatible Interfaces question.
 * This is a resource file, not part of the main codebase.
 * It is read by the main Muromuro server, and then its relevant parts are
 * replaced by the user-input solutions. Then the resulting code is sent to the
 * remote Docker container to do an evaluation of the user solution.
 */
public class IncompatibleInterfacesEval {

    private static final int[] RANDOM_NUMS_01 =
            {
                    23, 42, -14, 15, 29, 9, 30, -37, 22, -7,
                    36, -10, -38, -16, 40, -25, 24, -47, -20, -13,
                    16, 43, 28, 11, 31, -31, -1, 10, -40, 38,
                    7, -48, -42, -29, -5, 0, -21, -34, 44, -41,
                    33, 45, 50, -26, 49, -19, 37, 2, -44, 5,
                    35, -4, -22, 41, -36, -15, 4, -3, 6, 48,
                    3, -11, 21, -32, -2, 8, -35, -28, 13, -12,
                    -33, -9, -18, -27, -49, 1, 46, 47, -30, 20,
                    -46, -43, 27, 19, -17, 34, 26, -45, 18, 25,
                    12, -6, 14, 32, 39, -39, -24, 17, -8, -23,
            };

    private static final int[] RANDOM_NUMS_02 =
            {
                    23, 12, -45, 26, -39, 37, -12, 3, -22, 19,
                    6, 25, 35, -6, -5, -28, 4, -48, 11, 28,
                    20, -35, 30, -25, -46, -8, 13, 17, -14, 48,
                    1, -31, -42, -26, -41, -7, 5, 34, 31, -4,
                    -38, 40, 47, -34, 49, -49, -1, 0, -30, -33,
                    7, -16, 43, -47, -40, -36, 42, -17, 32, 36,
                    41, 2, -43, 18, 22, -2, -19, 8, 14, -37,
                    10, -23, -21, -15, -27, -13, 24, 44, -10, -29,
                    -32, 39, -3, 33, 27, -44, -20, 46, -24, 15,
                    21, -9, 9, 38, 50, 45, 29, -18, -11, 16,
            };

    private static final int[] RANDOM_NUMS_03 =
            {
                    2, -27, 38, 29, 36, 39, 28, 14, -20, 21,
                    15, 22, -3, 13, 16, 7, -39, 40, -26, -29,
                    -19, 30, 10, -41, -5, -34, -48, 24, -42, 19,
                    -11, -36, -49, -24, 0, 48, -18, 44, 9, 20,
                    11, -22, -38, 37, 1, -9, 25, -44, -15, -35,
                    33, -40, -37, -13, 5, 26, 23, 34, -16, -30,
                    42, -21, -8, 49, -31, 12, 3, 4, -1, 6,
                    -23, -7, -4, 32, -33, -28, -25, 18, -17, -12,
                    -43, 27, 17, 31, -32, 43, 35, -47, 46, -46,
                    50, 47, 45, -45, -6, -2, -14, 8, 41, -10,
            };

    public interface Processor {
        int run(int x, int y);
    }

    public static final class Processor1 implements Processor {
        @Override
        public int run(int x, int y) {
            return RANDOM_NUMS_01[y * 10 + x];
        }
    }

    public static final class Processor2 {
        public int makeCalculations(int x, int y) {
            return RANDOM_NUMS_02[y * 10 + x];
        }
    }

    public static final class Processor3 {
        public int execute(int x, int y) {
            return RANDOM_NUMS_03[y * 10 + x];
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


    public static final class MyClass {
        // Start main definition implementation.
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
        // End main definition implementation.
    }

    public void runTest1() {
        runTest(
                /* testId= */ "Test 1",
                /* yMin= */ 2,
                /* yMax= */ 5,
                /* xMin= */ 0,
                /* xMax= */ 3,
                /* expected= */ List.of(16, -36, 35, 7, -22, 30, 33, -40, -42),
                /* withoutOffsets= */ List.of(16, 30, 30, 7, -36, -42, 33, -22, 47));
    }

    public void runTest2() {
        runTest(
                /* testId= */ "Test 2",
                /* yMin= */ 6,
                /* yMax= */ 9,
                /* xMin= */ 3,
                /* xMax= */ 6,
                /* expected= */ List.of(-47, -33, 8, 18, -32, 1, -15, -6, 34),
                /* withoutOffsets= */ List.of(18, -31, 8, -15, -33, 1, 33, -32, 34));
    }

    public void runTest(
            String testId,
            int yMin,
            int yMax,
            int xMin,
            int xMax,
            List<Integer> expected,
            List<Integer> withoutOffsets
    ) {
        System.out.println("***** Running " + testId + ".");
        Processor processor1 = new Processor1();
        Processor2 processor2 = new Processor2();
        Processor3 processor3 = new Processor3();
        List<Integer> results = new ArrayList<>();
        int index = 0;
        for (int y = yMin; y < yMax; y++) {
            Client client = new Client(y);
            MyClass myClass =
                    new MyClass(client, processor1, processor2, processor3);
            for (int x = xMin; x < xMax; x++) {
                int result = myClass.runProcess(x);
                if (expected.get(index) != result) {
                    System.out.println(
                            "For x=" + x + ", y=" + y
                                    + ", result " + result
                                    + " does not match expected value "
                                    + expected.get(index));
                }
                results.add(result);
                index++;
            }
        }
        System.out.println();
        if (expected.equals(results)) {
            System.out.println(testId + " passed.");
        }
        else if (withoutOffsets.equals(results)) {
            System.out.println(testId + " failed. Offset adjustments to y values are missing.");
        }
        else {
            System.out.println(testId + " failed.");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        IncompatibleInterfacesEval eval = new IncompatibleInterfacesEval();
        eval.runTest1();
        eval.runTest2();
    }
}
