package com.muromuro.muromuro02.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class RepresentAccountStatesIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetRepresentAccountStates() throws Exception {
        this.mockMvc
                .perform(get("/muromuro_questions/represent_account_states"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Representing Account States")))
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
        this.mockMvc
                .perform(
                        post("/muromuro_questions/eval_represent_account_states")
                                .param("userInput", userInput))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Representing Account States")))
                .andExpect(content().string(containsString("Correct answer.")));
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
        this.mockMvc
                .perform(
                        post("/muromuro_questions/eval_represent_account_states")
                                .param("userInput", userInput))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Representing Account States")))
                .andExpect(content().string(containsString("Correct answer.")));
    }

    @Test
    public void testEval_withWrongAnswer_1() throws Exception {
        String userInput = "boolean isActive = true;";
        this.mockMvc
                .perform(
                        post("/muromuro_questions/eval_represent_account_states")
                                .param("userInput", userInput))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Representing Account States")))
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: Incorrect solution. "
                                                        + "(Hint: Can you use a better data type than boolean "
                                                        + "to represent the account states?)")));
    }

    @Test
    public void testEval_withWrongAnswer_2() throws Exception {
        String userInput = "int a = 5;";
        this.mockMvc
                .perform(
                        post("/muromuro_questions/eval_represent_account_states")
                                .param("userInput", userInput))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Representing Account States")))
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: Incorrect solution. "
                                                        + "(Hint: Can you think of a better data type "
                                                        + "to represent the account states?)")));
    }

    @Test
    public void testEval_withWrongAnswer_3() throws Exception {
        String userInput =
                "enum SomeEnumName {\n"
                        + "  Active,\n"
                        + "  Inactive\n"
                        + "}\n";
        this.mockMvc
                .perform(
                        post("/muromuro_questions/eval_represent_account_states")
                                .param("userInput", userInput))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Representing Account States")))
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: "
                                                        + "The solution does not seem to represent "
                                                        + "all the necessary account states, which are: "
                                                        + "ACTIVE, INACTIVE, SUSPENDED, DELETED")));
    }

    @Test
    public void testEval_withBadInput() throws Exception {
        String userInput = "blahblah";
        this.mockMvc
                .perform(
                        post("/muromuro_questions/eval_represent_account_states")
                                .param("userInput", userInput))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Representing Account States")))
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: The solution failed with syntax error.")));
    }

    @Test
    public void testEval_withInsecureInput_1() throws Exception {
        String userInput = "System.exit(0);";
        this.mockMvc
                .perform(
                        post("/muromuro_questions/eval_represent_account_states")
                                .param("userInput", userInput))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Representing Account States")))
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
        this.mockMvc
                .perform(
                        post("/muromuro_questions/eval_represent_account_states")
                                .param("userInput", userInput))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Representing Account States")))
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: The solution seems insecure, "
                                                        + "with forbidden keyword: Runtime")));
    }

    @Test
    public void testEval_withLengthyInput() throws Exception {
        int inputLength = 1001;
        String userInput =
                Stream.generate(() -> "a")
                        .limit(inputLength)
                        .collect(Collectors.joining());
        this.mockMvc
                .perform(
                        post("/muromuro_questions/eval_represent_account_states")
                                .param("userInput", userInput))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Representing Account States")))
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "The solution seems insecure, with length 1001 "
                                                        + "exceeding the max allowable length.")));
    }
}
