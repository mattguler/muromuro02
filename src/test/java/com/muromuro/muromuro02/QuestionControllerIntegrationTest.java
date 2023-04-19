package com.muromuro.muromuro02;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class QuestionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testListQuestions() throws Exception {
        this.mockMvc
                .perform(get("/muromuro_questions/list"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("The List of Muro Muro Questions")))
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Please select a question here below "
                                                        + "and attempt to answer it.")))
                .andExpect(content().string(containsString("Representing Account States")));
    }

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
    public void testEvalRepresentAccountStates_correctAnswer() throws Exception {
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
    public void testEvalRepresentAccountStates_wrongAnswer() throws Exception {
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
                                                "Wrong answer: The solution does not seem to "
                                                        + "represent all the necessary account states.")));
    }

    @Test
    public void testEvalRepresentAccountStates_badInput() throws Exception {
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
                                                "Wrong answer: The solution failed with error")));
    }
}
