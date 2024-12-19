import java.util.*;

/**
 * The initial solution for the DebugLists question.
 * This is a resource file, not part of the main codebase.
 * It is read by the main MuroMuro server, and then its relevant parts are
 * used by the server.
 */
public class DebugListSoln {

    // Start initial main definition implementation.
    private List<Integer> series;

    public DebugListSoln() {
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
    // End initial main definition implementation.

    public void printSeries(String title) {
        System.out.println(title);
        for (int num : series) {
            System.out.println(num);
        }
    }

    public static void main(String[] args) {
        DebugListSoln db1 = new DebugListSoln();
        db1.printSeries("Iteration 1:");
        DebugListSoln db2 = new DebugListSoln();
        db2.printSeries("Iteration 2:");
    }
}
