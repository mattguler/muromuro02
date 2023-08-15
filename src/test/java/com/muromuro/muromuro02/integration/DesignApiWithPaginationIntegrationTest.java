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

/** Runs integration tests for the Design API with Pagination Muromuro question. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class DesignApiWithPaginationIntegrationTest {

    private static final String GET_URL = "/muromuro_questions/design_api_with_pagination";
    private static final String POST_URL = "/muromuro_questions/eval_design_api_with_pagination";

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetDesignApiWithPagination() throws Exception {
        performGet()
                .andExpect(content().string(containsString("Design API with Pagination")))
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Please implement the call to your API function "
                                                        + "and your actual API function definition")));

    }

    @Test
    public void testEval_withInsecureInput_1() throws Exception {
        performPost("System.exit(0);", "")
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: The solution seems insecure, "
                                                        + "with forbidden keyword: System")));
    }

    @Test
    public void testEval_withInsecureInput_2() throws Exception {
        performPost("", "Runtime.getRuntime().exec(\"rm -rf /\");")
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: The solution seems insecure, "
                                                        + "with forbidden keyword: Runtime")));
    }

    @Test
    public void testEval_withBadlyFormedInput() throws Exception {
        performPost("", "// This is a comment")
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: The main definition should not "
                                                        + "start with a comment.")));
    }

    @Test
    public void testEval_withLengthyInput_1() throws Exception {
        performPost("a".repeat(2501), "")
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: The solution seems insecure, "
                                                        + "with length 2501 "
                                                        + "exceeding the max allowable length.")));
    }

    @Test
    public void testEval_withLengthyInput_2() throws Exception {
        performPost("", "a".repeat(2501))
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: The solution seems insecure, "
                                                        + "with length 2501 "
                                                        + "exceeding the max allowable length.")));
    }

    private ResultActions performGet() throws Exception {
        return this.mockMvc
                .perform(get(GET_URL))
                .andExpect(status().isOk());
    }

    private ResultActions performPost(String callerCode, String mainDefinition) throws Exception {
        return this.mockMvc
                .perform(
                        post(POST_URL)
                                .param("userInput.callerCode", callerCode)
                                .param("userInput.mainDefinition", mainDefinition))
                .andExpect(status().isOk());
    }
}
