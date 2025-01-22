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

/** Runs integration tests for the Design API with Pagination Muromuro question. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WithMockUser("interviewee")
@AutoConfigureMockMvc
public class DesignApiWithPaginationIntegrationTest {
    private static final String GET_URL = "/muromuro_questions/design_api_with_pagination";
    private static final String EVAL_URL = "/muromuro_questions/eval_design_api_with_pagination";

    private static final String CALLER_CODE_TEMPLATE =
            """
                    public void callerFunction(
                        List<Employee> list1,
                        List<Employee> list2,
                        List<Employee> list3,
                        List<Employee> list4,
                        int maxSize,
                        long companyId,
                        DatabaseProxy dbProxy
                    ) {
                        System.out.println();
                        List<Employee> results =
                                retrieveEmployees(
                                        dbProxy, companyId, /* offset= */ %s, %s);
                        list1.addAll(results);
                        results =
                                retrieveEmployees(
                                        dbProxy, companyId, /* offset= */ maxSize, maxSize);
                        list2.addAll(results);
                        results =
                                retrieveEmployees(
                                        dbProxy, companyId, /* offset= */ 2 * maxSize, maxSize);
                        list3.addAll(results);
                        results =
                                retrieveEmployees(
                                        dbProxy, companyId, /* offset= */ 3 * maxSize, maxSize);
                        list4.addAll(results);
                    }
                    """;

    private static final String MAIN_DEFINITION =
            """
                    public List<Employee> retrieveEmployees(
                            DatabaseProxy dbProxy,
                            long companyId,
                            int offset,
                            int maxSize) {
                        List<Employee> allEmployees = dbProxy.getEmployees(companyId);
                        List<Employee> results = new ArrayList<>();
                        for (int i = offset; i < allEmployees.size(); i++) {
                            results.add(allEmployees.get(i));
                            if (results.size() >= maxSize) {
                                break;
                            }
                        }
                        return results;
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
    public void testGetDesignApiWithPagination() throws Exception {
        performGet()
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Please implement the call to your API function "
                                                        + "and your actual API function definition")));

    }

    @Test
    public void testEval_withInsecureInput_1() throws Exception {
        performEval("System.exit(0);", "")
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: The solution seems insecure, "
                                                        + "with forbidden keyword: System")));
    }

    @Test
    public void testEval_withInsecureInput_2() throws Exception {
        performEval("", "Runtime.getRuntime().exec(\"rm -rf /\");")
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: The solution seems insecure, "
                                                        + "with forbidden keyword: Runtime")));
    }

    @Test
    public void testEval_inputWithImport() throws Exception {
        performEval("", "import java.util.*;")
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: The code should not contain "
                                                        + "any import statements.")));
    }

    @Test
    public void testEval_withLengthyInput_1() throws Exception {
        performEval("a".repeat(2501), "")
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: The solution seems insecure, with its "
                                                        + "length exceeding the max allowable length.")));
    }

    @Test
    public void testEval_withLengthyInput_2() throws Exception {
        performEval("", "a".repeat(2501))
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: The solution seems insecure, with its "
                                                        + "length exceeding the max allowable length.")));
    }

    @Test
    public void testEval_withEmptyInput() throws Exception {
        performEval("", "")
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: Invalid solution.")));
    }

    @Test
    public void testEval_withBadInput_1() throws Exception {
        performEval("blahblah", "")
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: Invalid solution.")));
    }

    @Test
    public void testEval_withBadInput_2() throws Exception {
        performEval("", "blahblah")
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: Invalid solution.")));
    }

    @Test
    public void testEval_withCorrectAnswer() throws Exception {
        String callerCode = String.format(CALLER_CODE_TEMPLATE, "0", "maxSize");
        performEval(callerCode, MAIN_DEFINITION)
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
        String callerCode = String.format(CALLER_CODE_TEMPLATE, "1", "maxSize");
        performEval(callerCode, MAIN_DEFINITION)
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: Incorrect solution. One or more of "
                                                        + "the lists are not\ncorrectly populated.")));
    }

    @Test
    public void testEval_withWrongAnswer_2() throws Exception {
        String callerCode = String.format(CALLER_CODE_TEMPLATE, "0", "maxSize - 1");
        performEval(callerCode, MAIN_DEFINITION)
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: Incorrect solution. One or more of "
                                                        + "the lists are not\ncorrectly populated.")));
    }

    @Test
    public void testEval_withWrongAnswer_3() throws Exception {
        String callerCode = String.format(CALLER_CODE_TEMPLATE, "0", "maxSize + 1");
        performEval(callerCode, MAIN_DEFINITION)
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: Incorrect solution. One or more of "
                                                        + "the lists are not\ncorrectly populated.")));
    }

    @Test
    public void testEval_withWrongAnswer_4() throws Exception {
        String callerCode =
                """
                        public void callerFunction(
                            List<Employee> list1,
                            List<Employee> list2,
                            List<Employee> list3,
                            List<Employee> list4,
                            int maxSize,
                            long companyId,
                            DatabaseProxy dbProxy
                        ) {
                            int i = 5;
                        }
                        """;
        performEval(callerCode, "boolean failureScenario = true;")
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: Incorrect solution. One or more of "
                                                        + "the lists are not\ncorrectly populated.")));
    }

    @Test
    public void testEval_withTimeout() throws Exception {
        String callerCode = String.format(CALLER_CODE_TEMPLATE, "0", "maxSize");
        String mainDefinition =
                """
                        List<Employee> retrieveEmployees(
                                DatabaseProxy dbProxy,
                                long companyId,
                                int offset,
                                int maxSize) {
                            List<Employee> allEmployees = dbProxy.getEmployees(companyId);
                            List<Employee> results = new ArrayList<>();
                            int a = 2;
                            while(a != 0) {
                                a = -a;
                            }
                            return results;
                        }
                        """;
        performEval(callerCode, mainDefinition)
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Server timed out: The solution took too long to execute.")));
    }

    private ResultActions performGet() throws Exception {
        return this.mockMvc
                .perform(get(GET_URL))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Design API with Pagination")));
    }

    private ResultActions performEval(String callerCode, String mainDefinition) throws Exception {
        return this.mockMvc
                .perform(
                        post(EVAL_URL)
                                .param("caller-code-input", callerCode)
                                .param("main-def-input", mainDefinition)
                                .with(csrf()))
                .andExpect(status().isOk());
    }
}
