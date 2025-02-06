import java.util.*;

/**
 * The evaluator for the DebugList question.
 * This is a resource file, not part of the main codebase.
 * It is read by the main MuroMuro server, and then its relevant parts are
 * replaced by the user-input solutions. Then the resulting code is sent to the
 * remote Docker container to do an evaluation of the user solution.
 */
public class DebugListEval {

    // Start main definition implementation.
    public static final class SeriesProcessor {

        private List<Integer> series;

        public SeriesProcessor() {
            series = createFiveElements();
            addFiveElements(series);
            removeFirstFiveElements(series);
        }

        private List<Integer> createFiveElements() {
            List<Integer> series = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                series.add(i);
            }
            return series;
        }

        // Series is assumed to be not empty.
        private void addFiveElements(List<Integer> series) {
            int lastNum = series.get(series.size() - 1);
            for (int i = 0; i < 5; i++) {
                series.add(lastNum + i);
            }
        }

        private void removeFirstFiveElements(List<Integer> series) {
            for (int i = 0; i < 5; i++) {
                series.remove(i);
            }
        }

        public List<Integer> getSeries() {
            return series;
        }

        public void printSeries(String title) {
            System.out.println(title);
            for (int num : series) {
                System.out.println(num);
            }
        }

        // This is not used in the actual evaluation of the solution.
        public static void main(String[] args) {
            SeriesProcessor sp1 = new SeriesProcessor();
            sp1.printSeries("Iteration 1:");
            SeriesProcessor sp2 = new SeriesProcessor();
            sp2.printSeries("Iteration 2:");
        }
    }
    // End main definition implementation.

    // Returns the test result as a string, to be printed later to stdout.
    public String runTest(SeriesProcessor sp, int testId) {
        if (sp.getSeries().size() != 5) {
            return String.format(
                    "Test for iteration %d failed. The list should have only 5 elements.",
                    testId);
        }
        for (int i = 0; i < 5; i++) {
            if (sp.getSeries().get(i) != i + (5 * testId)) {
                return String.format(
                        "Test for iteration %d failed. The list contains wrong numbers.",
                        testId);
            }
        }
        return String.format("Test for iteration %d passed.", testId);
    }

    public static void main(String[] args) {
        DebugListEval eval = new DebugListEval();
        SeriesProcessor sp1 = new SeriesProcessor();
        sp1.printSeries("Iteration 1:");
        String test1Result = eval.runTest(sp1, 1);
        SeriesProcessor sp2 = new SeriesProcessor();
        sp2.printSeries("Iteration 2");
        String test2Result = eval.runTest(sp2, 2);
        System.out.println();
        System.out.println(test1Result);
        System.out.println(test2Result);
    }
}
