package com.muromuro.muromuro02.service.evaluator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the EvaluationCode object. This is an integration test because
 * it requires the Spring Boot context to configure the EvaluationCode bean.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class EvaluationCodeTest {

    private static final String PAGINATION_API_CALLER_CODE =
            "List<Employee> employees = getAllEmployees(dbProxy);";
    private static final String PAGINATION_API_MAIN_DEFINITION =
            "static List<Employee> getAllEmployees(DatabaseProxy dbProxy) { return null; }";
    private static final String REPRESENT_ACCOUNT_STATES_MAIN_DEFINITION =
            "public enum SomeAccountState {}";

    @Autowired
    @Qualifier("designApiWithPagination")
    private EvaluationCode designApiWithPaginationEval;

    @Autowired
    @Qualifier("representAccountStates")
    private EvaluationCode representAccountStatesEval;

    @Test
    public void testGetContent_designApiWithPaginationEval() {
        String content = designApiWithPaginationEval.getFormattedContent();
        assertTrue(content.contains("public class DesignApiWithPaginationEval"));
        assertTrue(content.contains("class Employee"));
        assertTrue(content.contains("interface DatabaseProxy"));
        assertTrue(content.contains("class DatabaseProxyImpl"));
        assertTrue(
                content.contains(
                        "new Employee(id, \\\"first_name_\\\" + id, \\\"last_name_\\\" + id)"));
        assertTrue(
                content.contains(
                        "System.out.printf(\\\"%s is incorrectly formed.\\\\\\n\\\", listTitle);"));
        assertTrue(content.contains("printEmployees(\\\"List 1\\\", list1);"));
    }

    @Test
    public void testReplaceCallerCode_designApiWithPaginationEval() {
        String content = designApiWithPaginationEval
                .replaceCallerCode(PAGINATION_API_CALLER_CODE)
                .getFormattedContent();
        assertTrue(content.contains("public class DesignApiWithPaginationEval"));
        assertTrue(content.contains("class Employee"));
        assertTrue(content.contains(PAGINATION_API_CALLER_CODE));
        assertTrue(content.contains("public static void main(String[] args)"));
        assertTrue(content.contains("DatabaseProxy dbProxy = new DatabaseProxyImpl();"));
    }

    @Test
    public void testReplaceMainDefinition_designApiWithPaginationEval() {
        String content = designApiWithPaginationEval
                .replaceMainDefinition(PAGINATION_API_MAIN_DEFINITION)
                .getFormattedContent();
        assertTrue(content.contains("public class DesignApiWithPaginationEval"));
        assertTrue(content.contains("class Employee"));
        assertTrue(content.contains(PAGINATION_API_MAIN_DEFINITION));
        assertTrue(content.contains("public static void main(String[] args)"));
        assertTrue(content.contains("DatabaseProxy dbProxy = new DatabaseProxyImpl();"));
    }

    @Test
    public void testReplaceAll_designApiWithPaginationEval() {
        String content = designApiWithPaginationEval
                .replaceCallerCode(PAGINATION_API_CALLER_CODE)
                .replaceMainDefinition(PAGINATION_API_MAIN_DEFINITION)
                .getFormattedContent();
        assertTrue(content.contains("public class DesignApiWithPaginationEval"));
        assertTrue(content.contains("class Employee"));
        assertTrue(content.contains(PAGINATION_API_CALLER_CODE));
        assertTrue(content.contains(PAGINATION_API_MAIN_DEFINITION));
        assertTrue(content.contains("public static void main(String[] args)"));
        assertTrue(content.contains("DatabaseProxy dbProxy = new DatabaseProxyImpl();"));
    }

    @Test
    public void testGetContent_representAccountStatesEval() {
        String content = representAccountStatesEval.getFormattedContent();
        assertTrue(content.contains("System.out.println(\\\"Account state ACTIVE.\\\");"));
        assertTrue(content.contains("static void evalAccountState(AccountState accountState)"));
        assertTrue(content.contains("public static void main(String[] args)"));
    }

    @Test
    public void testReplaceMainDefinition_representAccountStatesEval() {
        String content = representAccountStatesEval
                .replaceMainDefinition(REPRESENT_ACCOUNT_STATES_MAIN_DEFINITION)
                .getFormattedContent();
        assertTrue(content.contains(REPRESENT_ACCOUNT_STATES_MAIN_DEFINITION));
        assertTrue(content.contains("static void evalAccountState(AccountState accountState)"));
        assertTrue(content.contains("public static void main(String[] args)"));
    }

    @Test
    public void testReplaceCallerCode_representAccountStatesEval() {
        Exception thrown = assertThrows(
                IllegalArgumentException.class,
                () -> {
                    representAccountStatesEval.replaceCallerCode("//");
                });
        assertTrue(thrown.getMessage().contains("Could not find the beginning or end sequence."));
    }
}
