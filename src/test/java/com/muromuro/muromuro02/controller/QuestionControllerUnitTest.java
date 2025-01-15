package com.muromuro.muromuro02.controller;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import com.muromuro.muromuro02.model.UserInput;
import com.muromuro.muromuro02.service.evaluator.Evaluator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// TODO: See if we can reduce some of the code duplication here.
/** Runs unit tests for the QuestionController. */
@WebMvcTest(QuestionController.class)
@WithMockUser("interviewee")
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

    @MockBean
    @Qualifier("detectSubstringsImpl")
    private Evaluator detectSubstrings;

    @MockBean
    @Qualifier("debugListImpl")
    private Evaluator debugList;

    @MockBean
    @Qualifier("parseCsvImpl")
    private Evaluator parseCsv;

    @MockBean
    @Qualifier("longRunningFunctionsImpl")
    private Evaluator longRunningFunctions;

    @MockBean
    @Qualifier("incompatibleInterfacesImpl")
    private Evaluator incompatibleInterfaces;

    @MockBean
    @Qualifier("duplicateRpcsImpl")
    private Evaluator duplicateRpcs;

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
                .andExpect(content().string(containsString("Device Database")))
                .andExpect(content().string(containsString("Detect Substrings")));
    }

    @Test
    public void testGetRepresentAccountStates() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/represent_account_states")
                        .setMainDefinition(INITIAL_MAIN_DEFINITION)
                        .setExpectedTitle("Representing Account States")
                        .setExpectedContent(
                                "How would you alter your code to represent "
                                        + "these new account states?")
                        .build();
        runGetTest(representAccountStates, testValues);
    }

    @Test
    public void testEvalRepresentAccountStates_correctAnswer() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/eval_represent_account_states")
                        .setMainDefinition(USER_INPUT)
                        .build();
        runEvalTest_correctAnswer(representAccountStates, testValues);
    }

    @Test
    public void testEvalRepresentAccountStates_wrongAnswer() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/eval_represent_account_states")
                        .setMainDefinition(USER_INPUT)
                        .build();
        runEvalTest_wrongAnswer(representAccountStates, testValues);
    }

    @Test
    public void testEvalRepresentAccountStates_timeout() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/eval_represent_account_states")
                        .setMainDefinition(USER_INPUT)
                        .build();
        runEvalTest_timeout(representAccountStates, testValues);
    }

    @Test
    public void testEvalRepresentAccountStates_unknownResponse() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/eval_represent_account_states")
                        .setMainDefinition(USER_INPUT)
                        .build();
        runEvalTest_unknownResponse(representAccountStates, testValues);
    }

    @Test
    public void testGetDesignApiWithPagination() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/design_api_with_pagination")
                        .setCallerCode(INITIAL_CALLER_CODE)
                        .setMainDefinition(INITIAL_MAIN_DEFINITION)
                        .setExpectedTitle("Design API with Pagination")
                        .setExpectedContent(
                                "Please implement the call to your API function "
                                        + "and your actual API function definition")
                        .build();
        runGetTest(designApiWithPagination, testValues);
    }

    @Test
    public void testEvalDesignApiWithPagination_correctAnswer() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/eval_design_api_with_pagination")
                        .setCallerCode(INITIAL_CALLER_CODE)
                        .setMainDefinition(USER_INPUT)
                        .build();
        runEvalTest_correctAnswer(designApiWithPagination, testValues);
    }

    @Test
    public void testEvalDesignApiWithPagination_wrongAnswer() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/eval_design_api_with_pagination")
                        .setCallerCode(INITIAL_CALLER_CODE)
                        .setMainDefinition(USER_INPUT)
                        .build();
        runEvalTest_wrongAnswer(designApiWithPagination, testValues);
    }

    @Test
    public void testEvalDesignApiWithPagination_timeout() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/eval_design_api_with_pagination")
                        .setCallerCode(INITIAL_CALLER_CODE)
                        .setMainDefinition(USER_INPUT)
                        .build();
        runEvalTest_timeout(designApiWithPagination, testValues);
    }

    @Test
    public void testEvalDesignApiWithPagination_unknownResponse() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/eval_design_api_with_pagination")
                        .setCallerCode(INITIAL_CALLER_CODE)
                        .setMainDefinition(USER_INPUT)
                        .build();
        runEvalTest_unknownResponse(designApiWithPagination, testValues);
    }

    @Test
    public void testGetRefactorTooManyIfs() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/refactor_too_many_ifs")
                        .setMainDefinition(INITIAL_MAIN_DEFINITION)
                        .setExpectedTitle("Refactoring Too Many Ifs")
                        .setExpectedContent(
                                "Could you please refactor this code in a "
                                        + "way that reduces the if statements")
                        .build();
        runGetTest(refactorTooManyIfs, testValues);
    }

    @Test
    public void testEvalRefactorTooManyIfs_correctAnswer() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/eval_refactor_too_many_ifs")
                        .setMainDefinition(USER_INPUT)
                        .build();
        runEvalTest_correctAnswer(refactorTooManyIfs, testValues);
    }

    @Test
    public void testEvalRefactorTooManyIfs_wrongAnswer() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/eval_refactor_too_many_ifs")
                        .setMainDefinition(USER_INPUT)
                        .build();
        runEvalTest_wrongAnswer(refactorTooManyIfs, testValues);
    }

    @Test
    public void testEvalRefactorTooManyIfs_timeout() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/eval_refactor_too_many_ifs")
                        .setMainDefinition(USER_INPUT)
                        .build();
        runEvalTest_timeout(refactorTooManyIfs, testValues);
    }

    @Test
    public void testEvalRefactorTooManyIfs_unknownResponse() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/eval_refactor_too_many_ifs")
                        .setMainDefinition(USER_INPUT)
                        .build();
        runEvalTest_unknownResponse(refactorTooManyIfs, testValues);
    }

    @Test
    public void testGetDeviceDatabase() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/device_database")
                        .setCallerCode(INITIAL_CALLER_CODE)
                        .setMainDefinition(INITIAL_MAIN_DEFINITION)
                        .setExpectedTitle("Device Database")
                        .setExpectedContent("which of these devices exists")
                        .build();
        runGetTest(deviceDatabase, testValues);
    }

    @Test
    public void testEvalDeviceDatabase_correctAnswer() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/eval_device_database")
                        .setCallerCode(INITIAL_CALLER_CODE)
                        .setMainDefinition(USER_INPUT)
                        .build();
        runEvalTest_correctAnswer(deviceDatabase, testValues);
    }

    @Test
    public void testEvalDeviceDatabase_wrongAnswer() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/eval_device_database")
                        .setCallerCode(INITIAL_CALLER_CODE)
                        .setMainDefinition(USER_INPUT)
                        .build();
        runEvalTest_wrongAnswer(deviceDatabase, testValues);
    }

    @Test
    public void testEvalDeviceDatabase_timeout() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/eval_device_database")
                        .setCallerCode(INITIAL_CALLER_CODE)
                        .setMainDefinition(USER_INPUT)
                        .build();
        runEvalTest_timeout(deviceDatabase, testValues);
    }

    @Test
    public void testEvalDeviceDatabase_unknownResponse() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/eval_device_database")
                        .setCallerCode(INITIAL_CALLER_CODE)
                        .setMainDefinition(USER_INPUT)
                        .build();
        runEvalTest_unknownResponse(deviceDatabase, testValues);
    }

    @Test
    public void testGetDetectSubstrings() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/detect_substrings")
                        .setMainDefinition(INITIAL_MAIN_DEFINITION)
                        .setExpectedTitle("Detect Substrings")
                        .setExpectedContent(
                                "implement a function that takes "
                                        + "a String object as an input")
                        .build();
        runGetTest(detectSubstrings, testValues);
    }

    @Test
    public void testEvalDetectSubstrings_correctAnswer() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/eval_detect_substrings")
                        .setMainDefinition(USER_INPUT)
                        .build();
        runEvalTest_correctAnswer(detectSubstrings, testValues);
    }

    @Test
    public void testEvalDetectSubstrings_wrongAnswer() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/eval_detect_substrings")
                        .setMainDefinition(USER_INPUT)
                        .build();
        runEvalTest_wrongAnswer(detectSubstrings, testValues);
    }

    @Test
    public void testEvalDetectSubstrings_timeout() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/eval_detect_substrings")
                        .setMainDefinition(USER_INPUT)
                        .build();
        runEvalTest_timeout(detectSubstrings, testValues);
    }

    @Test
    public void testEvalDetectSubstrings_unknownResponse() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/eval_detect_substrings")
                        .setMainDefinition(USER_INPUT)
                        .build();
        runEvalTest_unknownResponse(detectSubstrings, testValues);
    }

    @Test
    public void testGetDebugList() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/debug_list")
                        .setMainDefinition(INITIAL_MAIN_DEFINITION)
                        .setExpectedTitle("Debug List")
                        .setExpectedContent("code which contains a couple of bugs")
                        .build();
        runGetTest(debugList, testValues);
    }

    @Test
    public void testEvalDebugList_correctAnswer() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/eval_debug_list")
                        .setMainDefinition(USER_INPUT)
                        .build();
        runEvalTest_correctAnswer(debugList, testValues);
    }

    @Test
    public void testEvalDebugList_wrongAnswer() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/eval_debug_list")
                        .setMainDefinition(USER_INPUT)
                        .build();
        runEvalTest_wrongAnswer(debugList, testValues);
    }

    @Test
    public void testEvalDebugList_timeout() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/eval_debug_list")
                        .setMainDefinition(USER_INPUT)
                        .build();
        runEvalTest_timeout(debugList, testValues);
    }

    @Test
    public void testEvalDebugList_unknownResponse() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/eval_debug_list")
                        .setMainDefinition(USER_INPUT)
                        .build();
        runEvalTest_unknownResponse(debugList, testValues);
    }

    @Test
    public void testGetParseCsv() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/parse_csv")
                        .setMainDefinition(INITIAL_MAIN_DEFINITION)
                        .setExpectedTitle("Parse CSV")
                        .setExpectedContent(
                                "You are given a comma separated value (CSV) file string")
                        .build();
        runGetTest(parseCsv, testValues);
    }

    @Test
    public void testEvalParseCsv_correctAnswer() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/eval_parse_csv")
                        .setMainDefinition(USER_INPUT)
                        .build();
        runEvalTest_correctAnswer(parseCsv, testValues);
    }

    @Test
    public void testEvalParseCsv_wrongAnswer() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/eval_parse_csv")
                        .setMainDefinition(USER_INPUT)
                        .build();
        runEvalTest_wrongAnswer(parseCsv, testValues);
    }

    @Test
    public void testEvalParseCsv_timeout() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/eval_parse_csv")
                        .setMainDefinition(USER_INPUT)
                        .build();
        runEvalTest_timeout(parseCsv, testValues);
    }

    @Test
    public void testEvalParseCsv_unknownResponse() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/eval_parse_csv")
                        .setMainDefinition(USER_INPUT)
                        .build();
        runEvalTest_unknownResponse(parseCsv, testValues);
    }

    @Test
    public void testGetLongRunningFunctions() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/long_running_functions")
                        .setMainDefinition(INITIAL_MAIN_DEFINITION)
                        .setExpectedTitle("Long Running Functions")
                        .setExpectedContent(
                                "You are given a bunch of Black Box Processor objects")
                        .build();
        runGetTest(longRunningFunctions, testValues);
    }

    @Test
    public void testEvalLongRunningFunctions_correctAnswer() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/eval_long_running_functions")
                        .setMainDefinition(USER_INPUT)
                        .build();
        runEvalTest_correctAnswer(longRunningFunctions, testValues);
    }

    @Test
    public void testEvalLongRunningFunctions_wrongAnswer() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/eval_long_running_functions")
                        .setMainDefinition(USER_INPUT)
                        .build();
        runEvalTest_wrongAnswer(longRunningFunctions, testValues);
    }

    @Test
    public void testEvalLongRunningFunctions_timeout() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/eval_long_running_functions")
                        .setMainDefinition(USER_INPUT)
                        .build();
        runEvalTest_timeout(longRunningFunctions, testValues);
    }

    @Test
    public void testEvalLongRunningFunctions_unknownResponse() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/eval_long_running_functions")
                        .setMainDefinition(USER_INPUT)
                        .build();
        runEvalTest_unknownResponse(longRunningFunctions, testValues);
    }

    @Test
    public void testGetIncompatibleInterfaces() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/incompatible_interfaces")
                        .setMainDefinition(INITIAL_MAIN_DEFINITION)
                        .setExpectedTitle("Incompatible Interfaces")
                        .setExpectedContent(
                                "You now have to use more processors to get the job done.")
                        .build();
        runGetTest(incompatibleInterfaces, testValues);
    }

    @Test
    public void testEvalIncompatibleInterfaces_correctAnswer() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/eval_incompatible_interfaces")
                        .setMainDefinition(USER_INPUT)
                        .build();
        runEvalTest_correctAnswer_CM(incompatibleInterfaces, testValues);
    }

    @Test
    public void testEvalIncompatibleInterfaces_wrongAnswer() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/eval_incompatible_interfaces")
                        .setMainDefinition(USER_INPUT)
                        .build();
        runEvalTest_wrongAnswer_CM(incompatibleInterfaces, testValues);
    }

    @Test
    public void testEvalIncompatibleInterfaces_timeout() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/eval_incompatible_interfaces")
                        .setMainDefinition(USER_INPUT)
                        .build();
        runEvalTest_timeout_CM(incompatibleInterfaces, testValues);
    }

    @Test
    public void testEvalIncompatibleInterfaces_unknownResponse() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/eval_incompatible_interfaces")
                        .setMainDefinition(USER_INPUT)
                        .build();
        runEvalTest_unknownResponse_CM(incompatibleInterfaces, testValues);
    }

    @Test
    public void testGetDuplicateRpcs() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/duplicate_rpcs")
                        .setCallerCode(INITIAL_CALLER_CODE)
                        .setMainDefinition(INITIAL_MAIN_DEFINITION)
                        .setExpectedTitle("Duplicate RPCs")
                        .setExpectedContent(
                                "Please identify and fix this issue of duplicate RPC requests.")
                        .build();
        runGetTest(duplicateRpcs, testValues);
    }

    @Test
    public void testEvalDuplicateRpcs_correctAnswer() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/eval_duplicate_rpcs")
                        .setCallerCode(INITIAL_CALLER_CODE)
                        .setMainDefinition(USER_INPUT)
                        .build();
        runEvalTest_correctAnswer(duplicateRpcs, testValues);
    }

    @Test
    public void testEvalDuplicateRpcs_wrongAnswer() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/eval_duplicate_rpcs")
                        .setCallerCode(INITIAL_CALLER_CODE)
                        .setMainDefinition(USER_INPUT)
                        .build();
        runEvalTest_wrongAnswer(duplicateRpcs, testValues);
    }

    @Test
    public void testEvalDuplicateRpcs_timeout() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/eval_duplicate_rpcs")
                        .setCallerCode(INITIAL_CALLER_CODE)
                        .setMainDefinition(USER_INPUT)
                        .build();
        runEvalTest_timeout(duplicateRpcs, testValues);
    }

    @Test
    public void testEvalDuplicateRpcs_unknownResponse() throws Exception {
        TestValues testValues =
                new TestValues.Builder()
                        .setUrl("/muromuro_questions/eval_duplicate_rpcs")
                        .setCallerCode(INITIAL_CALLER_CODE)
                        .setMainDefinition(USER_INPUT)
                        .build();
        runEvalTest_unknownResponse(duplicateRpcs, testValues);
    }

    private record TestValues(
            String url,
            String callerCode,
            String mainDefinition,
            String expectedTitle,
            String expectedContent) {

        static final class Builder {
            private String url;
            private String callerCode;
            private String mainDefinition;
            private String expectedTitle;
            private String expectedContent;

            Builder() {
                url = "";
                callerCode = "";
                mainDefinition = "";
                expectedTitle = "";
                expectedContent = "";
            }

            Builder setUrl(String url) {
                this.url = url;
                return this;
            }

            Builder setCallerCode(String callerCode) {
                this.callerCode = callerCode;
                return this;
            }

            Builder setMainDefinition(String mainDefinition) {
                this.mainDefinition = mainDefinition;
                return this;
            }

            Builder setExpectedTitle(String expectedTitle) {
                this.expectedTitle = expectedTitle;
                return this;
            }

            Builder setExpectedContent(String expectedContent) {
                this.expectedContent = expectedContent;
                return this;
            }

            TestValues build() {
                return new TestValues(
                        url, callerCode, mainDefinition, expectedTitle, expectedContent);
            }
        }
    }

    private void runGetTest(
            Evaluator evaluator, TestValues testValues) throws Exception {
        when(evaluator.getInitialSolution())
                .thenReturn(
                        new UserInput(
                                testValues.callerCode(),
                                testValues.mainDefinition()));
        runGetTest_validateCommonParts(testValues);
    }

    private void runGetTest_validateCommonParts(
            TestValues testValues) throws Exception {
        ResultActions result =
                this.mockMvc
                        .perform(get(testValues.url()))
                        .andExpect(status().isOk())
                        .andExpect(
                                content()
                                        .string(
                                                containsString(
                                                        testValues.expectedTitle())));
        if (!testValues.callerCode().isEmpty()) {
            result
                    .andExpect(
                            content()
                                    .string(
                                            containsString(
                                                    testValues.callerCode())));
        }
        if (!testValues.mainDefinition().isEmpty()) {
            result
                    .andExpect(
                            content()
                                    .string(
                                            containsString(
                                                    testValues.mainDefinition())));
        }
        result
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                testValues.expectedContent())));
    }

    // TODO: Rename all the CodeMirror (CM) compatible versions of the methods below to
    //      original names once the CodeMirror migration is complete.
    //      Also remove all the old outdated methods.

    private void runEvalTest_correctAnswer_CM(
            Evaluator evaluator, TestValues testValues) throws Exception {
        when(evaluator.evaluateSolution(any(UserInput.class)))
                .thenReturn(new MuroMuroResponse(MuroMuroResponse.Status.SUCCESS));
        ResultActions result = sendEvalCommand_CM(testValues);
        result
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Correct answer")));
    }

    private void runEvalTest_correctAnswer(
            Evaluator evaluator, TestValues testValues) throws Exception {
        when(evaluator.evaluateSolution(any(UserInput.class)))
                .thenReturn(new MuroMuroResponse(MuroMuroResponse.Status.SUCCESS));
        ResultActions result = sendEvalCommand(testValues);
        result
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Correct answer")));
    }

    private void runEvalTest_wrongAnswer_CM(
            Evaluator evaluator, TestValues testValues) throws Exception {
        when(evaluator.evaluateSolution(any(UserInput.class)))
                .thenReturn(
                        new MuroMuroResponse(
                                MuroMuroResponse.Status.FAILURE,
                                ERROR_MESSAGE));
        ResultActions result = sendEvalCommand_CM(testValues);
        result
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: "
                                                        + ERROR_MESSAGE)));
    }

    private void runEvalTest_wrongAnswer(
            Evaluator evaluator, TestValues testValues) throws Exception {
        when(evaluator.evaluateSolution(any(UserInput.class)))
                .thenReturn(
                        new MuroMuroResponse(
                                MuroMuroResponse.Status.FAILURE,
                                ERROR_MESSAGE));
        ResultActions result = sendEvalCommand(testValues);
        result
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: "
                                                        + ERROR_MESSAGE)));
    }

    private void runEvalTest_timeout_CM(
            Evaluator evaluator, TestValues testValues) throws Exception {
        when(evaluator.evaluateSolution(any(UserInput.class)))
                .thenReturn(
                        new MuroMuroResponse(
                                MuroMuroResponse.Status.TIMEOUT,
                                TIMEOUT_MESSAGE));
        ResultActions result = sendEvalCommand_CM(testValues);
        result
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Server timed out: "
                                                        + TIMEOUT_MESSAGE)));
    }

    private void runEvalTest_timeout(
            Evaluator evaluator, TestValues testValues) throws Exception {
        when(evaluator.evaluateSolution(any(UserInput.class)))
                .thenReturn(
                        new MuroMuroResponse(
                                MuroMuroResponse.Status.TIMEOUT,
                                TIMEOUT_MESSAGE));
        ResultActions result = sendEvalCommand(testValues);
        result
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Server timed out: "
                                                        + TIMEOUT_MESSAGE)));
    }

    private void runEvalTest_unknownResponse_CM(
            Evaluator evaluator, TestValues testValues) throws Exception {
        when(evaluator.evaluateSolution(any(UserInput.class)))
                .thenReturn(
                        new MuroMuroResponse(
                                MuroMuroResponse.Status.UNKNOWN,
                                UNKNOWN_MESSAGE));
        ResultActions result = sendEvalCommand_CM(testValues);
        result
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Unknown response: "
                                                        + UNKNOWN_MESSAGE)));
    }

    private void runEvalTest_unknownResponse(
            Evaluator evaluator, TestValues testValues) throws Exception {
        when(evaluator.evaluateSolution(any(UserInput.class)))
                .thenReturn(
                        new MuroMuroResponse(
                                MuroMuroResponse.Status.UNKNOWN,
                                UNKNOWN_MESSAGE));
        ResultActions result = sendEvalCommand(testValues);
        result
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Unknown response: "
                                                        + UNKNOWN_MESSAGE)));
    }

    private ResultActions sendEvalCommand_CM(
            TestValues testValues) throws Exception {
        ResultActions result;
        if (testValues.callerCode().isEmpty()) {
            result =
                    this.mockMvc
                            .perform(
                                    post(testValues.url)
                                            .param(
                                                    "main-def-input",
                                                    testValues.mainDefinition())
                                            .with(csrf()))
                            .andExpect(status().isOk());
        } else {
            result =
                    this.mockMvc
                            .perform(
                                    post(testValues.url)
                                            .param(
                                                    "caller-code-input",
                                                    testValues.callerCode())
                                            .param(
                                                    "main-def-input",
                                                    testValues.mainDefinition())
                                            .with(csrf()))
                            .andExpect(status().isOk());
        }
        return result;
    }

    private ResultActions sendEvalCommand(
            TestValues testValues) throws Exception {
        ResultActions result;
        if (testValues.callerCode().isEmpty()) {
            result =
                    this.mockMvc
                            .perform(
                                    post(testValues.url)
                                            .param(
                                                    "userInput.mainDefinition",
                                                    testValues.mainDefinition())
                                            .with(csrf()))
                            .andExpect(status().isOk());
        } else {
            result =
                    this.mockMvc
                            .perform(
                                    post(testValues.url)
                                            .param(
                                                    "userInput.callerCode",
                                                    testValues.callerCode())
                                            .param(
                                                    "userInput.mainDefinition",
                                                    testValues.mainDefinition())
                                            .with(csrf()))
                            .andExpect(status().isOk());
        }
        return result;
    }
}
