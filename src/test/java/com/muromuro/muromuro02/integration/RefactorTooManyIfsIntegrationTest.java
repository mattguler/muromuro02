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

/** Runs integration tests for the RefactorTooManyIfs Muromuro question. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WithMockUser("interviewee")
@AutoConfigureMockMvc
public class RefactorTooManyIfsIntegrationTest {

    private static final String GET_URL = "/muromuro_questions/refactor_too_many_ifs";
    private static final String EVAL_URL = "/muromuro_questions/eval_refactor_too_many_ifs";

    private static final String WRONG_SOLUTION =
            """
                    public int doCalculation(String strInput, int intInput) {
                            int result = intInput;
                            if (strInput.equals("a")) {
                                result += 1;
                            }
                            else if (strInput.equals("b")) {
                                result -= 5;
                            }
                            else if (strInput.equals("c")) {
                                result *= 4;
                            }
                            else if (strInput.equals("d")) {
                                result *= result;
                            }
                            else if (strInput.equals("e")) {
                                result %= 10;
                            }
                            else if (strInput.equals("f")) {
                                result += 7;
                            }
                            else {
                                result = result;
                            }
                            return result;
                        }
                    """;

    private static final String CORRECT_SOLUTION =
            """
                    public int doCalculation(String strInput, int intInput) {
                            int result = intInput;
                            switch (strInput) {
                                case "a":
                                    result += 1;
                                    break;
                                case "b":
                                    result -= 5;
                                    break;
                                case "c":
                                    result *= 4;
                                    break;
                                case "d":
                                    result *= result;
                                    break;
                                case "e":
                                    result %= 10;
                                    break;
                                case "f":
                                    result += 7;
                                    break;
                                default:
                                    result = result;
                            }
                            return result;
                        }
                    """;

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
    public void testEval_inputWithImport() throws Exception {
        String userInput = "import java.util.*;";
        performEval(userInput)
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: The code should not contain "
                                                        + "any import statements.")));
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

    @Test
    public void testEval_withWrongAnswer_1() throws Exception {
        String userInput =
                "public int doCalculation(String strInput, int intInput) {\n"
                        + "  return 0;\n"
                        + "}\n";
        performEval(userInput)
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: Incorrect solution. "
                                                        + "The calculation output is\n"
                                                        + "wrong for some inputs.")));
    }

    @Test
    public void testEval_withWrongAnswer_2() throws Exception {
        performEval(WRONG_SOLUTION)
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: While the solution "
                                                        + "technically works,\nit still "
                                                        + "contains too many if statements.")));
    }

    @Test
    public void testEval_withCorrectAnswer() throws Exception {
        performEval(CORRECT_SOLUTION)
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Correct answer: The solution looks correct, "
                                                        + "but your interviewer will be the "
                                                        + "final judge.")));
    }

    private ResultActions performGet() throws Exception {
        return mockMvc
                .perform(get(GET_URL))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Refactoring Too Many Ifs")));
    }

    private ResultActions performEval(String mainDefinition) throws Exception {
        return mockMvc
                .perform(
                        post(EVAL_URL)
                                .param("main-def-input", mainDefinition)
                                .with(csrf()))
                .andExpect(status().isOk());
    }
}
