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
}
