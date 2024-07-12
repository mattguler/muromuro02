import java.util.*;
import java.util.stream.*;

/**
 * The evaluator for the DeviceDatabase question.
 * This is a resource file, not part of the main codebase.
 * It is read by the main MuroMuro server, and then its relevant parts are
 * replaced by the user-input solutions. Then the resulting code is sent to the
 * remote Docker container to do an evaluation of the user solution.
 */
public class DeviceDatabaseEval {
    static final Map<String, List<String>> DEVICE_DATABASE_MAP =
            Map.ofEntries(
                    Map.entry(
                            "abc",
                            List.of("abcxyzwu", "abcdefgh", "abca2b5c")),
                    Map.entry(
                            "xyz",
                            List.of("xyza1b2c", "xyzqwert", "xyzabcde", "xyzxyzwu")));

    interface DeviceDatabase {
        List<String> ListDevices(String clusterName);
        int getCallCount();
    }

    static class DeviceDatabaseImpl implements DeviceDatabase {

        protected int callCount = 0;

        DeviceDatabaseImpl() {
            callCount = 0;
        }

        @Override
        public List<String> ListDevices(String clusterName) {
            callCount++;
            if (!DEVICE_DATABASE_MAP.containsKey(clusterName)) {
                return List.of();
            }
            return DEVICE_DATABASE_MAP.get(clusterName);
        }

        @Override
        public int getCallCount() {
            return callCount;
        }
    }

    static class NullReturningDeviceDatabaseImpl extends DeviceDatabaseImpl {

        @Override
        public List<String> ListDevices(String clusterName) {
            callCount++;
            return DEVICE_DATABASE_MAP.get(clusterName);
        }
    }

    // For debugging purposes only.
    void printList(List<String> list) {
        System.out.println(String.join(", ", list));
    }

    public void callerFunction(
            List<String> inputDevices,
            DeviceDatabase ddb,
            List<String> resultDevicesInDdb,
            List<String> resultDevicesNotInDdb) {
        // Start caller code implementation.
        // End caller code implementation.
    }

    // Start main definition implementation.
    // End main definition implementation.

    // This is a generic functional test with one cluster that does not exist in db.
    public void runTest1() {
        List<String> inputDevices =
                List.of(
                        "xyzabcde",
                        "xyzhgfds",
                        "abcabcde",
                        "xyza1b2c",
                        "abcdefgh",
                        "zzwabcde");
        DeviceDatabase ddb = new DeviceDatabaseImpl();
        List<String> resultDevicesInDdb = new ArrayList<>();
        List<String> resultDevicesNotInDdb = new ArrayList<>();
        callerFunction(inputDevices, ddb, resultDevicesInDdb, resultDevicesNotInDdb);
        if (!areEqual(
                Set.of("xyza1b2c", "xyzabcde", "abcdefgh"),
                resultDevicesInDdb)) {
            System.out.println("Test 1 failed.");
        }
        else {
            if (!areEqual(
                    Set.of("xyzhgfds", "zzwabcde", "abcabcde"),
                    resultDevicesNotInDdb)) {
                System.out.println("Test 1 partially working.");
            }
            else {
                System.out.println("Test 1 passed.");
            }
        }
        System.out.println("Test 1 call count: " + ddb.getCallCount());
    }

    // This is a test with empty input.
    public void runTest2() {
        List<String> inputDevices = List.of();
        DeviceDatabase ddb = new DeviceDatabaseImpl();
        List<String> resultDevicesInDdb = new ArrayList<>();
        List<String> resultDevicesNotInDdb = new ArrayList<>();
        callerFunction(inputDevices, ddb, resultDevicesInDdb, resultDevicesNotInDdb);
        if (resultDevicesInDdb.isEmpty() && resultDevicesNotInDdb.isEmpty()) {
            System.out.println("Test 2 passed.");
        }
        else {
            System.out.println("Test 2 failed.");
        }
        System.out.println("Test 2 call count: " + ddb.getCallCount());
    }

    // This is a test with a badly formed input.
    public void runTest3() {
        List<String> inputDevices = List.of("xyza1b2c", "abcdefgh", "xy");
        DeviceDatabase ddb = new DeviceDatabaseImpl();
        List<String> resultDevicesInDdb = new ArrayList<>();
        List<String> resultDevicesNotInDdb = new ArrayList<>();
        try {
            callerFunction(inputDevices, ddb, resultDevicesInDdb, resultDevicesNotInDdb);
        } catch (StringIndexOutOfBoundsException e) {
            System.out.println("Test 3 failed with StringIndexOutOfBoundsException.");
            System.out.println("Test 3 call count: " + ddb.getCallCount());
            return;
        }
        if (!areEqual(Set.of("xyza1b2c", "abcdefgh"), resultDevicesInDdb)) {
            System.out.println("Test 3 failed.");
        }
        else {
            if (!areEqual(Set.of("xy"), resultDevicesNotInDdb)) {
                System.out.println("Test 3 partially working.");
            }
            else {
                System.out.println("Test 3 passed.");
            }
        }
        System.out.println("Test 3 call count: " + ddb.getCallCount());
    }

    // This is a test with a device database that might return a null result.
    public void runTest4() {
        List<String> inputDevices = List.of("xyza1b2c", "abcdefgh", "zzwabcde");
        DeviceDatabase ddb = new NullReturningDeviceDatabaseImpl();
        List<String> resultDevicesInDdb = new ArrayList<>();
        List<String> resultDevicesNotInDdb = new ArrayList<>();
        try {
            callerFunction(inputDevices, ddb, resultDevicesInDdb, resultDevicesNotInDdb);
        } catch (NullPointerException e) {
            System.out.println("Test 4 failed with NullPointerException.");
            System.out.println("Test 4 call count: " + ddb.getCallCount());
            return;
        }
        if (!areEqual(Set.of("xyza1b2c", "abcdefgh"), resultDevicesInDdb)) {
            System.out.println("Test 4 failed.");
        }
        else {
            if (!areEqual(Set.of("zzwabcde"), resultDevicesNotInDdb)) {
                System.out.println("Test 4 partially working.");
            }
            else {
                System.out.println("Test 4 passed.");
            }
        }
        System.out.println("Test 4 call count: " + ddb.getCallCount());
    }

    /**
     * Returns true if the given list contains the same devices as the given
     * expected set of devices, regardless of the order in the list.
     */
    boolean areEqual(
            Set<String> expectedDevices, List<String> devices) {
        Set<String> devicesSet = new HashSet<>(devices);
        return expectedDevices.equals(devicesSet);
    }

    public static void main(String[] args) {
        DeviceDatabaseEval eval = new DeviceDatabaseEval();
        eval.runTest1();
        eval.runTest2();
        eval.runTest3();
        eval.runTest4();
    }
}
