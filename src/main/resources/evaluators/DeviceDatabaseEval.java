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

    public static void main(String[] args) {
        System.out.println("Device Database Eval not yet implemented.");
    }
}
