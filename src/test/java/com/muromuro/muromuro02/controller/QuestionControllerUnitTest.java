package com.muromuro.muromuro02.controller;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import com.muromuro.muromuro02.model.UserInput;
import com.muromuro.muromuro02.service.evaluator.Evaluator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Runs unit tests for the QuestionController. */
@WebMvcTest(QuestionController.class)
public class QuestionControllerUnitTest {

    private static final String INITIAL_CALLER_CODE = "initial caller code";
    private static final String INITIAL_MAIN_DEFINITION = "initial main definition";
    private static final String USER_INPUT = "user input";
    private static final String ERROR_MESSAGE = "error message";
    private static final String TIMEOUT_MESSAGE = "timeout message";
    private static final String UNKNOWN_MESSAGE = "unknown message";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    @Qualifier("representAccountStatesImpl")
    private Evaluator representAccountStates;

    @MockBean
    @Qualifier("designApiWithPaginationImpl")
    private Evaluator designApiWithPagination;

    @MockBean
    @Qualifier("refactorTooManyIfsImpl")
    private Evaluator refactorTooManyIfs;

    @MockBean
    @Qualifier("deviceDatabaseImpl")
    private Evaluator deviceDatabase;

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
                .andExpect(content().string(containsString("Representing Account States")))
                .andExpect(content().string(containsString("Design API with Pagination")))
                .andExpect(content().string(containsString("Refactoring Too Many Ifs")))
                .andExpect(content().string(containsString("Device Database")));
    }

    @Test
    public void testGetRepresentAccountStates() throws Exception {
        when(representAccountStates.getInitialSolution())
                .thenReturn(new UserInput("", INITIAL_MAIN_DEFINITION));
        this.mockMvc
                .perform(get("/muromuro_questions/represent_account_states"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Representing Account States")))
                .andExpect(content().string(containsString(INITIAL_MAIN_DEFINITION)))
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "How would you alter your code to represent "
                                                        + "these new account states?")));
    }

    @Test
    public void testEvalRepresentAccountStates_correctAnswer() throws Exception {
        when(representAccountStates.evaluateSolution(any(UserInput.class)))
                .thenReturn(new MuroMuroResponse(MuroMuroResponse.Status.SUCCESS));
        this.mockMvc
                .perform(
                        post("/muromuro_questions/eval_represent_account_states")
                                .param("userInput.mainDefinition", USER_INPUT))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Representing Account States")))
                .andExpect(content().string(containsString("Correct answer.")));
    }

    @Test
    public void testEvalRepresentAccountStates_wrongAnswer() throws Exception {
        when(representAccountStates.evaluateSolution(any(UserInput.class)))
                .thenReturn(
                        new MuroMuroResponse(
                                MuroMuroResponse.Status.FAILURE,
                                ERROR_MESSAGE));
        this.mockMvc
                .perform(
                        post("/muromuro_questions/eval_represent_account_states")
                                .param("userInput.mainDefinition", USER_INPUT))
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
        when(representAccountStates.evaluateSolution(any(UserInput.class)))
                .thenReturn(
                        new MuroMuroResponse(
                                MuroMuroResponse.Status.TIMEOUT,
                                TIMEOUT_MESSAGE));
        this.mockMvc
                .perform(
                        post("/muromuro_questions/eval_represent_account_states")
                                .param("userInput.mainDefinition", USER_INPUT))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Representing Account States")))
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Server timed out: "
                                                        + TIMEOUT_MESSAGE)));
    }

    @Test
    public void testEvalRepresentAccountStates_unknownResponse() throws Exception {
        when(representAccountStates.evaluateSolution(any(UserInput.class)))
                .thenReturn(
                        new MuroMuroResponse(
                                MuroMuroResponse.Status.UNKNOWN,
                                UNKNOWN_MESSAGE));
        this.mockMvc
                .perform(
                        post("/muromuro_questions/eval_represent_account_states")
                                .param("userInput.mainDefinition", USER_INPUT))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Representing Account States")))
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Unknown response: "
                                                        + UNKNOWN_MESSAGE)));
    }

    @Test
    public void testGetDesignApiWithPagination() throws Exception {
        when(designApiWithPagination.getInitialSolution())
                .thenReturn(new UserInput(INITIAL_CALLER_CODE, INITIAL_MAIN_DEFINITION));
        this.mockMvc
                .perform(get("/muromuro_questions/design_api_with_pagination"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Design API with Pagination")))
                .andExpect(content().string(containsString(INITIAL_CALLER_CODE)))
                .andExpect(content().string(containsString(INITIAL_MAIN_DEFINITION)))
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Please implement the call to your API function "
                                                        + "and your actual API function definition")));
    }

    @Test
    public void testEvalDesignApiWithPagination_correctAnswer() throws Exception {
        when(designApiWithPagination.evaluateSolution(any(UserInput.class)))
                .thenReturn(new MuroMuroResponse(MuroMuroResponse.Status.SUCCESS));
        this.mockMvc
                .perform(
                        post("/muromuro_questions/eval_design_api_with_pagination")
                                .param("userInput.callerCode", INITIAL_CALLER_CODE)
                                .param("userInput.mainDefinition", USER_INPUT))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Design API with Pagination")))
                .andExpect(content().string(containsString("Correct answer.")));
    }

    @Test
    public void testEvalDesignApiWithPagination_wrongAnswer() throws Exception {
        when(designApiWithPagination.evaluateSolution(any(UserInput.class)))
                .thenReturn(
                        new MuroMuroResponse(
                                MuroMuroResponse.Status.FAILURE,
                                ERROR_MESSAGE));
        this.mockMvc
                .perform(
                        post("/muromuro_questions/eval_design_api_with_pagination")
                                .param("userInput.callerCode", INITIAL_CALLER_CODE)
                                .param("userInput.mainDefinition", USER_INPUT))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Design API with Pagination")))
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: "
                                                        + ERROR_MESSAGE)));
    }

    @Test
    public void testEvalDesignApiWithPagination_timeout() throws Exception {
        when(designApiWithPagination.evaluateSolution(any(UserInput.class)))
                .thenReturn(
                        new MuroMuroResponse(
                                MuroMuroResponse.Status.TIMEOUT,
                                TIMEOUT_MESSAGE));
        this.mockMvc
                .perform(
                        post("/muromuro_questions/eval_design_api_with_pagination")
                                .param("userInput.callerCode", INITIAL_CALLER_CODE)
                                .param("userInput.mainDefinition", USER_INPUT))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Design API with Pagination")))
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Server timed out: "
                                                        + TIMEOUT_MESSAGE)));
    }

    @Test
    public void testEvalDesignApiWithPagination_unknownResponse() throws Exception {
        when(designApiWithPagination.evaluateSolution(any(UserInput.class)))
                .thenReturn(
                        new MuroMuroResponse(
                                MuroMuroResponse.Status.UNKNOWN,
                                UNKNOWN_MESSAGE));
        this.mockMvc
                .perform(
                        post("/muromuro_questions/eval_design_api_with_pagination")
                                .param("userInput.callerCode", INITIAL_CALLER_CODE)
                                .param("userInput.mainDefinition", USER_INPUT))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Design API with Pagination")))
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Unknown response: "
                                                        + UNKNOWN_MESSAGE)));
    }

    @Test
    public void testGetRefactorTooManyIfs() throws Exception {
        when(refactorTooManyIfs.getInitialSolution())
                .thenReturn(new UserInput("", INITIAL_MAIN_DEFINITION));
        this.mockMvc
                .perform(get("/muromuro_questions/refactor_too_many_ifs"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Refactoring Too Many Ifs")))
                .andExpect(content().string(containsString(INITIAL_MAIN_DEFINITION)))
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Could you please refactor this code in a "
                                                        + "way that reduces the if statements")));
    }

    @Test
    public void testEvalRefactorTooManyIfs_correctAnswer() throws Exception {
        when(refactorTooManyIfs.evaluateSolution(any(UserInput.class)))
                .thenReturn(new MuroMuroResponse(MuroMuroResponse.Status.SUCCESS));
        this.mockMvc
                .perform(
                        post("/muromuro_questions/eval_refactor_too_many_ifs")
                                .param("userInput.mainDefinition", USER_INPUT))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Refactoring Too Many Ifs")))
                .andExpect(content().string(containsString("Correct answer")));
    }

    @Test
    public void testEvalRefactorTooManyIfs_wrongAnswer() throws Exception {
        when(refactorTooManyIfs.evaluateSolution(any(UserInput.class)))
                .thenReturn(
                        new MuroMuroResponse(
                                MuroMuroResponse.Status.FAILURE,
                                ERROR_MESSAGE));
        this.mockMvc
                .perform(
                        post("/muromuro_questions/eval_refactor_too_many_ifs")
                                .param("userInput.mainDefinition", USER_INPUT))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Refactoring Too Many Ifs")))
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: "
                                                        + ERROR_MESSAGE)));
    }

    @Test
    public void testEvalRefactorTooManyIfs_timeout() throws Exception {
        when(refactorTooManyIfs.evaluateSolution(any(UserInput.class)))
                .thenReturn(
                        new MuroMuroResponse(
                                MuroMuroResponse.Status.TIMEOUT,
                                TIMEOUT_MESSAGE));
        this.mockMvc
                .perform(
                        post("/muromuro_questions/eval_refactor_too_many_ifs")
                                .param("userInput.mainDefinition", USER_INPUT))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Refactoring Too Many Ifs")))
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Server timed out: "
                                                        + TIMEOUT_MESSAGE)));
    }

    @Test
    public void testEvalRefactorTooManyIfs_unknownResponse() throws Exception {
        when(refactorTooManyIfs.evaluateSolution(any(UserInput.class)))
                .thenReturn(
                        new MuroMuroResponse(
                                MuroMuroResponse.Status.UNKNOWN,
                                UNKNOWN_MESSAGE));
        this.mockMvc
                .perform(
                        post("/muromuro_questions/eval_refactor_too_many_ifs")
                                .param("userInput.mainDefinition", USER_INPUT))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Refactoring Too Many Ifs")))
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Unknown response: "
                                                        + UNKNOWN_MESSAGE)));
    }

    @Test
    public void testGetDeviceDatabase() throws Exception {
        when(deviceDatabase.getInitialSolution())
                .thenReturn(new UserInput(INITIAL_CALLER_CODE, INITIAL_MAIN_DEFINITION));
        this.mockMvc
                .perform(get("/muromuro_questions/device_database"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Device Database")))
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "which of these devices exists")))
                .andExpect(content().string(containsString(INITIAL_CALLER_CODE)))
                .andExpect(content().string(containsString(INITIAL_MAIN_DEFINITION)));
    }

    @Test
    public void testEvalDeviceDatabase_correctAnswer() throws Exception {
        when(deviceDatabase.evaluateSolution(any(UserInput.class)))
                .thenReturn(new MuroMuroResponse(MuroMuroResponse.Status.SUCCESS));
        this.mockMvc
                .perform(
                        post("/muromuro_questions/eval_device_database")
                                .param("userInput.callerCode", INITIAL_CALLER_CODE)
                                .param("userInput.mainDefinition", USER_INPUT))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Device Database")))
                .andExpect(content().string(containsString("Correct answer")));
    }

    @Test
    public void testEvalDeviceDatabase_wrongAnswer() throws Exception {
        when(deviceDatabase.evaluateSolution(any(UserInput.class)))
                .thenReturn(
                        new MuroMuroResponse(
                                MuroMuroResponse.Status.FAILURE,
                                ERROR_MESSAGE));
        this.mockMvc
                .perform(
                        post("/muromuro_questions/eval_device_database")
                                .param("userInput.callerCode", INITIAL_CALLER_CODE)
                                .param("userInput.mainDefinition", USER_INPUT))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Device Database")))
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: "
                                                        + ERROR_MESSAGE)));
    }

    @Test
    public void testEvalDeviceDatabase_timeout() throws Exception {
        when(deviceDatabase.evaluateSolution(any(UserInput.class)))
                .thenReturn(
                        new MuroMuroResponse(
                                MuroMuroResponse.Status.TIMEOUT,
                                TIMEOUT_MESSAGE));
        this.mockMvc
                .perform(
                        post("/muromuro_questions/eval_device_database")
                                .param("userInput.callerCode", INITIAL_CALLER_CODE)
                                .param("userInput.mainDefinition", USER_INPUT))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Device Database")))
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Server timed out: "
                                                        + TIMEOUT_MESSAGE)));
    }

    @Test
    public void testEvalDeviceDatabase_unknownResponse() throws Exception {
        when(deviceDatabase.evaluateSolution(any(UserInput.class)))
                .thenReturn(
                        new MuroMuroResponse(
                                MuroMuroResponse.Status.UNKNOWN,
                                UNKNOWN_MESSAGE));
        this.mockMvc
                .perform(
                        post("/muromuro_questions/eval_device_database")
                                .param("userInput.callerCode", INITIAL_CALLER_CODE)
                                .param("userInput.mainDefinition", USER_INPUT))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Device Database")))
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Unknown response: "
                                                        + UNKNOWN_MESSAGE)));
    }
}
