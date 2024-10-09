package com.muromuro.muromuro02.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Integration tests for the Device Database Muromuro question. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class DeviceDatabaseIntegrationTest {

    private static final String GET_URL = "/muromuro_questions/device_database";
    private static final String EVAL_URL = "/muromuro_questions/eval_device_database";

    private static final String CALLER_CODE =
            "areDevicesInDdb(inputDevices, ddb, resultDevicesInDdb, resultDevicesNotInDdb);";

    private static final String MAIN_DEFINITION_TEMPLATE =
            """
                    public void areDevicesInDdb(
                            List<String> inputDevices,
                            DeviceDatabase ddb,
                            List<String> resultDevicesInDdb,
                            List<String> resultDevicesNotInDdb) {
            
                        System.out.println(inputDevices);
                        Map<String, List<String>> devicesMap = new HashMap<>();
                        %s
            
                        for (String device : inputDevices) {
                            String clusterId = getClusterId(device);
                            List<String> devicesInDdb;
                            if (devicesMap.containsKey(clusterId)) {
                                devicesInDdb = devicesMap.get(clusterId);
                            }
                            else {
                                devicesInDdb = ddb.ListDevices(clusterId);
                                %s {
                                    %s
                                    continue;
                                }
                                devicesMap.put(clusterId, devicesInDdb);
                            }
                            if (devicesInDdb.contains(device)) {
                                resultDevicesInDdb.add(device);
                            }
                            else {
                                %s
                            }
                        }
                    }
            
                    private String getClusterId(String device) {
                        %s
                        return device.substring(0, 3);
                    }
            """;

    private static final String ADD_WRONG_DEVICES_TO_DB =
            "resultDevicesInDdb.addAll(inputDevices);";

    private static final String WRONG_IF_EMPTY =
            """
                        if (inputDevices.isEmpty()) {
                            // Because the quotes do not really work well here.
                            resultDevicesInDdb.add(String.valueOf(15));
                        }
            """;

    private static final String CHECK_FOR_NULL =
            "if (devicesInDdb == null || devicesInDdb.isEmpty())";

    private static final String NO_CHECK_FOR_NULL =
            "if (devicesInDdb.isEmpty())";

    private static final String ADD_DEVICES_NOT_IN_DB =
            "resultDevicesNotInDdb.add(device);";

    private static final String DEVICE_LENGTH_CHECK =
            """
                        if (device.length() < 3) {
                            return device;
                        }
            """;

    private static String getCorrectSolution() {
        return String.format(
                MAIN_DEFINITION_TEMPLATE,
                "",
                CHECK_FOR_NULL,
                ADD_DEVICES_NOT_IN_DB,
                ADD_DEVICES_NOT_IN_DB,
                DEVICE_LENGTH_CHECK);
    }

    private static String getWrongSolution() {
        return String.format(
                MAIN_DEFINITION_TEMPLATE,
                ADD_WRONG_DEVICES_TO_DB,
                CHECK_FOR_NULL,
                ADD_DEVICES_NOT_IN_DB,
                ADD_DEVICES_NOT_IN_DB,
                DEVICE_LENGTH_CHECK);
    }

    private static String getSolutionWithMissingOutputList() {
        return String.format(
                MAIN_DEFINITION_TEMPLATE,
                "",
                CHECK_FOR_NULL,
                "",
                "",
                DEVICE_LENGTH_CHECK);
    }

    private static String getSolutionWithWrongEmptyList() {
        return String.format(
                MAIN_DEFINITION_TEMPLATE,
                WRONG_IF_EMPTY,
                CHECK_FOR_NULL,
                ADD_DEVICES_NOT_IN_DB,
                ADD_DEVICES_NOT_IN_DB,
                DEVICE_LENGTH_CHECK);
    }

    private static String getSolutionWithNoCheckForNull() {
        return String.format(
                MAIN_DEFINITION_TEMPLATE,
                "",
                NO_CHECK_FOR_NULL,
                ADD_DEVICES_NOT_IN_DB,
                ADD_DEVICES_NOT_IN_DB,
                DEVICE_LENGTH_CHECK);
    }

    private static String getSolutionWithNoDeviceLengthCheck() {
        return String.format(
                MAIN_DEFINITION_TEMPLATE,
                "",
                CHECK_FOR_NULL,
                ADD_DEVICES_NOT_IN_DB,
                ADD_DEVICES_NOT_IN_DB,
                "");
    }

    private static final String INEFFICIENT_SOLUTION =
            """
                    public void areDevicesInDdb(
                            List<String> inputDevices,
                            DeviceDatabase ddb,
                            List<String> resultDevicesInDdb,
                            List<String> resultDevicesNotInDdb) {
            
                        for (String device : inputDevices) {
                            String clusterId = getClusterId(device);
                            List<String> devicesInDdb = ddb.ListDevices(clusterId);
                            if (devicesInDdb == null || devicesInDdb.isEmpty()) {
                                resultDevicesNotInDdb.add(device);
                                continue;
                            }
                            if (devicesInDdb.contains(device)) {
                                resultDevicesInDdb.add(device);
                            }
                            else {
                                resultDevicesNotInDdb.add(device);
                            }
                        }
                    }
            
                    static String getClusterId(String device) {
                        if (device.length() < 3) {
                            return device;
                        }
                        return device.substring(0, 3);
                    }
            """;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetDeviceDatabase() throws Exception {
        performGet()
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "You are given a long list of devices "
                                                        + "in the following form.")));
    }

    @Test
    public void testEval_withInsecureInput_1() throws Exception {
        performEval("System.exit(0);", "")
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: The solution seems insecure, "
                                                        + "with forbidden keyword: System")));
    }

    @Test
    public void testEval_withInsecureInput_2() throws Exception {
        performEval("", "Runtime.getRuntime().exec(\"rm -rf /\");")
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: The solution seems insecure, "
                                                        + "with forbidden keyword: Runtime")));
    }

    @Test
    public void testEval_inputWithImport() throws Exception {
        performEval("", "import java.util.*;")
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: The code should not contain "
                                                        + "any import statements.")));
    }

    @Test
    public void testEval_withLengthyInput_1() throws Exception {
        performEval("a".repeat(2501), "")
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: The solution seems insecure, with its "
                                                        + "length exceeding the max allowable length.")));
    }

    @Test
    public void testEval_withLengthyInput_2() throws Exception {
        performEval("", "a".repeat(2501))
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: The solution seems insecure, with its "
                                                        + "length exceeding the max allowable length.")));
    }

    @Test
    public void testEval_withBadInput_1() throws Exception {
        performEval("blahblah", "")
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: Invalid solution.")));
    }

    @Test
    public void testEval_withBadInput_2() throws Exception {
        performEval("", "blahblah")
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: Invalid solution.")));
    }

    @Test
    public void testEval_withTimeout() throws Exception {
        String callerCode = "while (true) { }";
        performEval(callerCode, "")
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Server timed out: The solution took too long to execute.")));
    }

    @Test
    public void testEval_withEmptyInput() throws Exception {
        performEval("", "")
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: Incorrect solution.")));
    }

    @Test
    public void testEval_withWrongAnswer() throws Exception {
        performEval(CALLER_CODE, getWrongSolution())
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: Incorrect solution.")));
    }

    @Test
    public void testEval_withAnswerMissingOutputList() throws Exception {
        performEval(CALLER_CODE, getSolutionWithMissingOutputList())
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: Incomplete solution.\n"
                                                        + "One of the output lists is not correctly populated.")));
    }

    @Test
    public void testEval_withWrongEmptyList() throws Exception {
        performEval(CALLER_CODE, getSolutionWithWrongEmptyList())
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: Incorrect solution. Your solution is not\n"
                                                        + "correctly handling the case of an empty input list.")));
    }

    @Test
    public void testEval_withNoCheckForNull() throws Exception {
        performEval(CALLER_CODE, getSolutionWithNoCheckForNull())
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Incorrect solution. Please consider that\n"
                                                        + "the device database method might return a null.")));
    }

    @Test
    public void testEval_withNoDeviceLengthCheck() throws Exception {
        performEval(CALLER_CODE, getSolutionWithNoDeviceLengthCheck())
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Incorrect solution. Please consider that\n"
                                                        + "the input might be badly formed.")));
    }

    @Test
    public void testEval_withInefficientSolution() throws Exception {
        performEval(CALLER_CODE, INEFFICIENT_SOLUTION)
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Inefficient solution. While the solution "
                                                        + "output looks\ncorrect, it can still "
                                                        + "be improved to run faster.")));
    }

    @Test
    public void testEval_withCorrectAnswer() throws Exception {
        performEval(CALLER_CODE, getCorrectSolution())
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Correct answer: The solution looks correct, "
                                                        + "but your interviewer will be the "
                                                        + "final judge.")));
    }

    private ResultActions performGet() throws Exception {
        return this.mockMvc
                .perform(get(GET_URL))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Device Database")));
    }

    private ResultActions performEval(
            String callerCode, String mainDefinition) throws Exception {
        return this.mockMvc
                .perform(
                        post(EVAL_URL)
                                .param("userInput.callerCode", callerCode)
                                .param("userInput.mainDefinition", mainDefinition))
                .andExpect(status().isOk());
    }
}
