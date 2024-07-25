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

/** Integration tests for the Detect Substrings Muromuro question. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class DetectSubstringsIntegrationTest {

    private static final String GET_URL = "/muromuro_questions/detect_substrings";
    private static final String EVAL_URL = "/muromuro_questions/eval_detect_substrings";

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetDetectSubstrings() throws Exception {
        performGet()
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "implement a function that takes "
                                                        + "a String object as an input")));
    }

    @Test
    public void testEval_withInsecureInput_1() throws Exception {
        performEval("System.exit(0);")
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: The solution seems insecure, "
                                                        + "with forbidden keyword: System")));
    }

    @Test
    public void testEval_withInsecureInput_2() throws Exception {
        performEval("Runtime.getRuntime().exec(\"rm -rf /\");")
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: The solution seems insecure, "
                                                        + "with forbidden keyword: Runtime")));
    }

    @Test
    public void testEval_inputWithImport() throws Exception {
        performEval("import java.util.*;")
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: The code should not contain "
                                                        + "any import statements.")));
    }

    @Test
    public void testEval_withLengthyInput() throws Exception {
        performEval("a".repeat(2001))
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: The solution seems insecure, with its "
                                                        + "length exceeding the max allowable length.")));
    }

    // TODO: Remove this test once the evaluator is fully implemented.
    @Test
    public void testEval_withUnknownResponse() throws Exception {
        performEval("blahblah")
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Unknown response: Detect Substrings "
                                                        + "evaluator not yet implemented.")));
    }

    // TODO: Implement the rest of the integration tests here for this question.

    private ResultActions performGet() throws Exception {
        return mockMvc
                .perform(get(GET_URL))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Detect Substrings")));
    }

    private ResultActions performEval(String mainDefinition) throws Exception {
        return this.mockMvc
                .perform(
                        post(EVAL_URL)
                                .param("userInput.mainDefinition", mainDefinition))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Detect Substrings")));
    }
}
