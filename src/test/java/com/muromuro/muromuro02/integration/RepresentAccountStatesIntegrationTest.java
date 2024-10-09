package com.muromuro.muromuro02.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Runs integration tests for the Represent Account States MuroMuro question. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class RepresentAccountStatesIntegrationTest {

    private static final String GET_URL = "/muromuro_questions/represent_account_states";
    private static final String EVAL_URL = "/muromuro_questions/eval_represent_account_states";

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetRepresentAccountStates() throws Exception {
        performGet()
                .andExpect(content().string(containsString("boolean isActive = false;")))
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "How would you alter your code to represent "
                                                        + "these new account states?")));
    }

    @Test
    public void testEval_withCorrectAnswer_1() throws Exception {
        String userInput =
                "enum AccountState {\n"
                + "  INACTIVE,\n"
                + "  ACTIVE,\n"
                + "  SUSPENDED,\n"
                + "  DELETED\n"
                + "}\n";
        performEval(userInput)
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Correct answer: The solution looks correct, "
                                                        + "but your interviewer will be the "
                                                        + "final judge.")));
    }

    @Test
    public void testEval_withCorrectAnswer_2() throws Exception {
        String userInput =
                "enum SomeEnumName {\n"
                        + "  Active,\n"
                        + "  Inactive,\n"
                        + "  Deleted,\n"
                        + "  Suspended\n"
                        + "}\n";
        performEval(userInput)
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Correct answer: The solution looks correct, "
                                                        + "but your interviewer will be the "
                                                        + "final judge.")));
    }

    @Test
    public void testEval_withWrongAnswer_1() throws Exception {
        String userInput = "boolean isActive = true;";
        performEval(userInput)
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: Incorrect solution.\n"
                                                        + "(Hint: Can you use a better data type than boolean\n"
                                                        + "to represent the account states?)")));
    }

    @Test
    public void testEval_withWrongAnswer_2() throws Exception {
        String userInput = "int a = 5;";
        performEval(userInput)
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: Incorrect solution.\n"
                                                        + "(Hint: Can you think of a better data type\n"
                                                        + "to represent the account states?)")));
    }

    @Test
    public void testEval_withWrongAnswer_3() throws Exception {
        String userInput =
                "enum SomeEnumName {\n"
                        + "  Active,\n"
                        + "  Inactive\n"
                        + "}\n";
        performEval(userInput)
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: "
                                                        + "The solution does not seem to represent\n"
                                                        + "all the necessary account states, which are:\n"
                                                        + "ACTIVE, INACTIVE, SUSPENDED, DELETED")));
    }

    @Test
    public void testEval_withInvalidDefinition() throws Exception {
        String userInput =
                "for (int i=0; i<5; i++) {\n"
                        + "  int b = 7;\n"
                        + "}\n";
        performEval(userInput)
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: Invalid definition.")));
    }

    @Test
    public void testEval_withBadInput() throws Exception {
        String userInput = "blahblah";
        performEval(userInput)
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: Invalid definition.")));
    }

    @Test
    public void testEval_withTimeout() throws Exception {
        String userInput =
                "enum AccountState {\n"
                        + "  INACTIVE,\n"
                        + "  ACTIVE,\n"
                        + "  SUSPENDED,\n"
                        + "  DELETED;\n"
                        + "  \n"
                        + "  private final int a;\n"
                        + "  \n"
                        + "  AccountState() {\n"
                        + "    a = 2;\n"
                        + "    int i = 5;\n"
                        + "    int b = 7;\n"
                        + "    while (i>0) {\n"
                        + "      b = -b;\n"
                        + "    }\n"
                        + "  }\n"
                        + "}\n";
        performEval(userInput)
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Server timed out: The solution took too long to execute.")));
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
        String userInput = "Runtime.getRuntime().exec(\"rm -rf /\")";
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
        int inputLength = 1001;
        String userInput =
                Stream.generate(() -> "a")
                        .limit(inputLength)
                        .collect(Collectors.joining());
        performEval(userInput)
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "The solution seems insecure, with its length "
                                                        + "exceeding the max allowable length.")));
    }

    private ResultActions performGet() throws Exception {
        return this.mockMvc
                .perform(get(GET_URL))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Representing Account States")));
    }

    private ResultActions performEval(String mainDefinition) throws Exception {
        return this.mockMvc
                .perform(
                        post(EVAL_URL)
                                .param("userInput.mainDefinition", mainDefinition))
                .andExpect(status().isOk());
    }
}
