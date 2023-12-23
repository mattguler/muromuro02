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

/** Runs integration tests for the RefactorTooManyIfs Muromuro question. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class RefactorTooManyIfsIntegrationTest {

    private static final String GET_URL = "/muromuro_questions/refactor_too_many_ifs";
    private static final String EVAL_URL = "/muromuro_questions/eval_refactor_too_many_ifs";

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetRefactorTooManyIfs() throws Exception {
        performGet()
                .andExpect(content().string(containsString("Refactoring Too Many Ifs")))
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Could you please refactor this code in a "
                                                        + "way that reduces the if statements")))
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "public int doCalculation(String strInput, int intInput)")))
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "result += 1;")));
    }

    // TODO: Remove this once the evaluator implementation is complete.
//    @Test
//    public void testEval_withUnimplementedEvaluator() throws Exception {
//        String userInput = "public int doCalculation(String strInput, int intInput) { return 0; }";
//        performEval(userInput)
//                .andExpect(
//                        content()
//                                .string(
//                                        containsString(
//                                                "Unknown response: Evaluator "
//                                                        + "not fully implemented yet.")));
//    }

    @Test
    public void testEval_withInsecureInput_1() throws Exception {
        String userInput = "System.exit(0);";
        performEval(userInput)
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: The solution seems insecure, "
                                                        + "with forbidden keyword: System")));
    }

    @Test
    public void testEval_withInsecureInput_2() throws Exception {
        String userInput = "Runtime.getRuntime().exec(\"rm -rf /\");";
        performEval(userInput)
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: The solution seems insecure, "
                                                        + "with forbidden keyword: Runtime")));
    }

    @Test
    public void testEval_withBadlyFormedInput_1() throws Exception {
        String userInput = "// This is a comment";
        performEval(userInput)
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: The main definition should not "
                                                        + "start with a comment.")));
    }

    @Test
    public void testEval_withLengthyInput() throws Exception {
        String userInput = "a".repeat(2501);
        performEval(userInput)
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: The solution seems insecure, with its "
                                                        + "length exceeding the max allowable length.")));
    }

    @Test
    public void testEval_withEmptyInput() throws Exception {
        String userInput = "";
        performEval(userInput)
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: Invalid solution.")));
    }

    @Test
    public void testEval_withBadInput_1() throws Exception {
        String userInput = "blahblah";
        performEval(userInput)
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: Invalid solution.")));
    }

    @Test
    public void testEval_withBadInput_2() throws Exception {
        String userInput = "public int doCalculation() { return 0; }";
        performEval(userInput)
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: Invalid solution.")));
    }

    @Test
    public void testEval_withTimeout() throws Exception {
        String userInput =
                "public int doCalculation(String strInput, int intInput) {\n"
                        + "  int a = 5;\n"
                        + "  while (a != 0) {\n"
                        + "    a = -a;\n"
                        + "  }\n"
                        + "  return 0;\n"
                        + "}\n";
        performEval(userInput)
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Server timed out: The solution took "
                                                        +"too long to execute.")));
    }

    private ResultActions performGet() throws Exception {
        return mockMvc
                .perform(get(GET_URL))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Refactoring Too Many Ifs")));
    }

    private ResultActions performEval(String mainDefinition) throws Exception {
        return mockMvc
                .perform(post(EVAL_URL).param("userInput.mainDefinition", mainDefinition))
                .andExpect(status().isOk());
    }
}
