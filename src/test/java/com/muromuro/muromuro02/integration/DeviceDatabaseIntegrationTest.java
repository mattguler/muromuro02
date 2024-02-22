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
    public void testEval_inputWithComment() throws Exception {
        performEval("", "// This is a comment")
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: The main definition should not "
                                                        + "start with a comment.")));
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

    // TODO: Implement more tests as the evaluator itself is implemented.

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
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Device Database")));
    }
}
