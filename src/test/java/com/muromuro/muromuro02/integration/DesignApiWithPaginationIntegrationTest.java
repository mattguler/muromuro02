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

/** Runs integration tests for the Design API with Pagination Muromuro question. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class DesignApiWithPaginationIntegrationTest {
    private static final String GET_URL = "/muromuro_questions/design_api_with_pagination";
    private static final String EVAL_URL = "/muromuro_questions/eval_design_api_with_pagination";

    private static final String CALLER_CODE_TEMPLATE =
            "System.out.println();\n" +
                    "List<Employee> results =\n" +
                    "        retrieveEmployees(\n" +
                    "                dbProxy, companyId, /* offset= */ %s, %s);\n" +
                    "list1.addAll(results);\n" +
                    "results =\n" +
                    "        retrieveEmployees(\n" +
                    "                dbProxy, companyId, /* offset= */ maxSize, maxSize);\n" +
                    "list2.addAll(results);\n" +
                    "results =\n" +
                    "        retrieveEmployees(\n" +
                    "                dbProxy, companyId, /* offset= */ 2 * maxSize, maxSize);\n" +
                    "list3.addAll(results);\n" +
                    "results =\n" +
                    "        retrieveEmployees(\n" +
                    "                dbProxy, companyId, /* offset= */ 3 * maxSize, maxSize);\n" +
                    "list4.addAll(results);";

    private static final String MAIN_DEFINITION =
            "public List<Employee> retrieveEmployees(\n" +
                    "        DatabaseProxy dbProxy,\n" +
                    "        long companyId,\n" +
                    "        int offset,\n" +
                    "        int maxSize) {\n" +
                    "    List<Employee> allEmployees = dbProxy.getEmployees(companyId);\n" +
                    "    List<Employee> results = new ArrayList<>();\n" +
                    "    for (int i = offset; i < allEmployees.size(); i++) {\n" +
                    "        results.add(allEmployees.get(i));\n" +
                    "        if (results.size() >= maxSize) {\n" +
                    "            break;\n" +
                    "        }\n" +
                    "    }\n" +
                    "    return results;\n" +
                    "}";

    @Autowired
    private MockMvc mockMvc;

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
    public void testEval_inputWithComment() throws Exception {
        performEval("", "// This is a comment")
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: The main definition should not "
                                                        + "start with a comment.")));
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
                                                "Wrong answer: Incorrect solution. One or more of "
                                                        + "the lists are not\ncorrectly populated.")));
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
                                                "Correct answer.")));
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
        performEval("int i = 5;", "boolean failureScenario = true;")
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
                "List<Employee> retrieveEmployees(\n" +
                        "        DatabaseProxy dbProxy,\n" +
                        "        long companyId,\n" +
                        "        int offset,\n" +
                        "        int maxSize) {\n" +
                        "    List<Employee> allEmployees = dbProxy.getEmployees(companyId);\n" +
                        "    List<Employee> results = new ArrayList<>();\n" +
                        "    int a = 2;\n" +
                        "    while(a != 0) {\n" +
                        "        a = -a;\n" +
                        "    }\n" +
                        "    return results;\n" +
                        "}";
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
                                .param("userInput.callerCode", callerCode)
                                .param("userInput.mainDefinition", mainDefinition))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Design API with Pagination")));
    }
}
