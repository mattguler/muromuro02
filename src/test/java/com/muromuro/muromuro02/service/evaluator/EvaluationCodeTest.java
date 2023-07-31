package com.muromuro.muromuro02.service.evaluator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

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

    @Autowired
    @Qualifier("designApiWithPagination")
    private EvaluationCode designApiWithPaginationEval;

    @Test
    public void testGetContent_designApiWithPaginationEval() {
        String content = designApiWithPaginationEval.getContent();
        assertTrue(content.contains("public class DesignApiWithPaginationEval"));
        assertTrue(content.contains("class Employee"));
        assertTrue(content.contains("interface DatabaseProxy"));
        assertTrue(content.contains("class DatabaseProxyImpl"));
    }

    @Test
    public void testReplaceCallerCode_designApiWithPaginationEval() {
        String content = designApiWithPaginationEval
                .replaceCallerCode(PAGINATION_API_CALLER_CODE)
                .getContent();
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
                .getContent();
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
                .getContent();
        assertTrue(content.contains("public class DesignApiWithPaginationEval"));
        assertTrue(content.contains("class Employee"));
        assertTrue(content.contains(PAGINATION_API_CALLER_CODE));
        assertTrue(content.contains(PAGINATION_API_MAIN_DEFINITION));
        assertTrue(content.contains("public static void main(String[] args)"));
        assertTrue(content.contains("DatabaseProxy dbProxy = new DatabaseProxyImpl();"));
    }
}
