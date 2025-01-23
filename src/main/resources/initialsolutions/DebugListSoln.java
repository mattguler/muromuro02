import java.util.*;

/**
 * The initial solution for the DebugLists question.
 * This is a resource file, not part of the main codebase.
 * It is read by the main MuroMuro server, and then its relevant parts are
 * used by the server.
 */
public class DebugListSoln {

    // Start initial main definition implementation.
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

        // Do not make any changes below these lines.

        public List<Integer> getSeries() {
            return series;
        }

        public void printSeries(String title) {
            System.out.println(title);
            for (int num : series) {
                System.out.println(num);
            }
        }

        public static void main(String[] args) {
            SeriesProcessor sp1 = new SeriesProcessor();
            sp1.printSeries("Iteration 1:");
            SeriesProcessor sp2 = new SeriesProcessor();
            sp2.printSeries("Iteration 2:");
        }
    }
    // End initial main definition implementation.
}
