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

/** Integration tests for the Parse CSV Muromuro question. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class ParseCsvIntegrationTest {

    private static final String GET_URL = "/muromuro_questions/parse_csv";
    private static final String EVAL_URL = "/muromuro_questions/eval_parse_csv";

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetParseCsv() throws Exception {
        performGet()
                .validateContains("Parse CSV")
                .validateContains(
                        "You are given a comma separated value (CSV) file string")
                .validateContains(
                        "Here are a few caveats and important points");
    }

    @Test
    public void testEval_withInsecureInput_1() throws Exception {
        performEval("System.exit(0);")
                .validateContains(
                        "Wrong answer: The solution seems insecure, "
                                + "with forbidden keyword: System");
    }

    @Test
    public void testEval_withInsecureInput_2() throws Exception {
        performEval("Runtime.getRuntime().exec(\\\"rm -rf /\\\");")
                .validateContains(
                        "Wrong answer: The solution seems insecure, "
                                + "with forbidden keyword: Runtime");
    }

    @Test
    public void testEval_withImport() throws Exception {
        performEval("import java.util.*;")
                .validateContains(
                        "Wrong answer: The code should not contain "
                                + "any import statements.");
    }

    @Test
    public void testEval_withLengthyInput() throws Exception {
        performEval("a".repeat(4001))
                .validateContains(
                        "Wrong answer: The solution seems insecure, with its "
                                + "length exceeding the max allowable length.");
    }

    // TODO: Remove this test once the evaluator is fully implemented.
    @Test
    public void testEval_withUnknownResponse() throws Exception {
        performEval("blahblah")
                .validateContains(
                        "Unknown response: Parse CSV evaluator "
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
                                        .param("userInput.mainDefinition", mainDefinition))
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
