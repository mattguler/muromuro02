package com.muromuro.muromuro02.controller;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import com.muromuro.muromuro02.service.evaluator.Evaluator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuestionController.class)
public class QuestionControllerUnitTest {

    private static final String INITIAL_SOLUTION = "initial solution";
    private static final String USER_INPUT = "user input";
    private static final String ERROR_MESSAGE = "error message";
    private static final String TIMEOUT_MESSAGE = "timeout message";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    @Qualifier("representAccountStatesImpl")
    private Evaluator representAccountStates;

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
        when(representAccountStates.getInitialSolution()).thenReturn(INITIAL_SOLUTION);
        this.mockMvc
                .perform(get("/muromuro_questions/represent_account_states"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Representing Account States")))
                .andExpect(content().string(containsString(INITIAL_SOLUTION)))
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "How would you alter your code to represent "
                                                        + "these new account states?")));
    }

    @Test
    public void testEvalRepresentAccountStates_correctAnswer() throws Exception {
        when(representAccountStates.evaluateSolution(USER_INPUT))
                .thenReturn(new MuroMuroResponse(MuroMuroResponse.Status.SUCCESS));
        this.mockMvc
                .perform(
                        post("/muromuro_questions/eval_represent_account_states")
                                .param("userInput", USER_INPUT))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Representing Account States")))
                .andExpect(content().string(containsString("Correct answer.")));
    }

    @Test
    public void testEvalRepresentAccountStates_wrongAnswer() throws Exception {
        when(representAccountStates.evaluateSolution(USER_INPUT))
                .thenReturn(
                        new MuroMuroResponse(
                                MuroMuroResponse.Status.FAILURE,
                                ERROR_MESSAGE));
        this.mockMvc
                .perform(
                        post("/muromuro_questions/eval_represent_account_states")
                                .param("userInput", USER_INPUT))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Representing Account States")))
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: "
                                                        + ERROR_MESSAGE)));
    }

    @Test
    public void testEvalRepresentAccountStates_timeout() throws Exception {
        when(representAccountStates.evaluateSolution(USER_INPUT))
                .thenReturn(
                        new MuroMuroResponse(
                                MuroMuroResponse.Status.TIMEOUT,
                                TIMEOUT_MESSAGE));
        this.mockMvc
                .perform(
                        post("/muromuro_questions/eval_represent_account_states")
                                .param("userInput", USER_INPUT))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Representing Account States")))
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Unknown response: "
                                                        + TIMEOUT_MESSAGE)));
    }
}
