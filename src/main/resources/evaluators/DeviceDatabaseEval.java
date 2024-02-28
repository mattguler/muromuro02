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
    interface DeviceDatabase {
        List<String> ListDevices(String clusterName);
    }

    static class DeviceDatabaseImpl implements DeviceDatabase {

        private static final Map<String, List<String>> DEVICE_DATABASE_MAP =
                Map.ofEntries(
                        Map.entry(
                                "abc",
                                List.of("abcxyzwu", "abcdefgh")),
                        Map.entry(
                                "xyz",
                                List.of("xyza1b2c", "xyzabcdef")));

        @Override
        public List<String> ListDevices(String clusterName) {
            return DEVICE_DATABASE_MAP.get(clusterName);
        }
    }

    // For debugging purposes only.
    static void printList(List<String> list) {
        System.out.println(String.join(", ", list));
    }

    static void callerFunction(
            List<String> inputDevices,
            DeviceDatabase ddb,
            List<String> resultDevicesInDdb,
            List<String> resultDevicesNotInDdb) {
        // Start caller code implementation.
        // End caller code implementation.
    }

    // Start main definition implementation.
    // End main definition implementation.

    static void runTest1() {
        List<String> inputDevices =
                List.of("xyza1b2c", "abcdefgh", "zzwfoobr", "xyzhgfed");
        DeviceDatabase ddb = new DeviceDatabaseImpl();
        List<String> resultDevicesInDdb = new ArrayList<>();
        List<String> resultDevicesNotInDdb = new ArrayList<>();
        callerFunction(inputDevices, ddb, resultDevicesInDdb, resultDevicesNotInDdb);
        System.out.println("Devices in DDB:");
        printList(resultDevicesInDdb);
        System.out.println("Devices not in DDB:");
        printList(resultDevicesNotInDdb);
    }

    public static void main(String[] args) {
        runTest1();
    }
}
