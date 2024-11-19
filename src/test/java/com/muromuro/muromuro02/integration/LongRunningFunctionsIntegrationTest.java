package com.muromuro.muromuro02.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WithMockUser("interviewee")
@AutoConfigureMockMvc
public class LongRunningFunctionsIntegrationTest {

    private static final String GET_URL = "/muromuro_questions/long_running_functions";
    private static final String EVAL_URL = "/muromuro_questions/eval_long_running_functions";

    @Autowired
    WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc =
                MockMvcBuilders
                        .webAppContextSetup(context)
                        .apply(springSecurity())
                        .build();
    }

    @Test
    public void testGetLongRunningFunctions() throws Exception {
        performGet()
                .validateContains("Long Running Functions")
                .validateContains(
                        "You are given a bunch of Black Box Processor objects");
    }

    @Test
    public void testEval_withInsecureInput() throws Exception {
        performEval("Runtime.getRuntime().exec(\\\"rm -rf /\\\");")
                .validateContains(
                        "Wrong answer: The solution seems insecure, "
                                + "with forbidden keyword: Runtime");
    }

    @Test
    public void testEval_withLengthyInput() throws Exception {
        performEval("a".repeat(5001))
                .validateContains(
                        "Wrong answer: The solution seems insecure, with its "
                                + "length exceeding the max allowable length.");
    }

    // TODO: Remove this test once the evaluator is fully implemented.
    @Test
    public void testEval_withUnknownResponse() throws Exception {
        performEval("blahblah")
                .validateContains(
                        "Unknown response: Long Running Functions evaluator "
                                + "not yet implemented.");
    }

    private ResultWrapper performGet() throws Exception {
        ResultActions resultActions =
                this.mockMvc
                        .perform(get(GET_URL))
                        .andExpect(status().isOk());
        return new ResultWrapper(resultActions);
    }

    private ResultWrapper performEval(String mainDefinition) throws Exception {
        ResultActions resultActions =
                this.mockMvc
                        .perform(
                                post(EVAL_URL)
                                        .param("userInput.mainDefinition", mainDefinition)
                                        .with(csrf()))
                        .andExpect(status().isOk());
        return new ResultWrapper(resultActions);
    }

    /** This is here to decrease code clutter in the integration tests. */
    private record ResultWrapper(ResultActions resultActions) {

        ResultWrapper validateContains(String str) throws Exception {
            this.resultActions().andExpect(content().string(containsString(str)));
            return this;
        }
    }
}
