package com.muromuro.muromuro02.service.evaluator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the InitialSolution object. This is an integration test because
 * it requires the Spring Boot context to configure the InitialSolution bean.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class InitialSolutionTest {

    @Autowired
    @Qualifier("debugListSoln")
    private InitialSolution debugListSoln;

    @Autowired
    @Qualifier("longRunningFunctionsSoln")
    private InitialSolution longRunningFunctionsSoln;

    @Autowired
    @Qualifier("refactorTooManyIfsSoln")
    private InitialSolution refactorTooManyIfsSoln;

    @Test
    public void testGetContent_debugListSoln() {
        String content = debugListSoln.getRelevantContent();
        assertTrue(content.contains("List<Integer> createFiveElements()"));
        assertTrue(content.contains("void addFiveElements(List<Integer> series)"));
        assertTrue(content.contains("void removeFirstFiveElements(List<Integer> series)"));
    }

    @Test
    public void testGetContent_longRunningFunctionsSoln() {
        String content = longRunningFunctionsSoln.getRelevantContent();
        assertTrue(content.contains("public boolean runBlackBoxProcesses("));
        assertTrue(
                content.contains(
                        "(acct, amount) -> acct.setValue(acct.getValue() + amount)"));
    }

    @Test
    public void testGetContent_refactorTooManyIfsSoln() {
        String content = refactorTooManyIfsSoln.getRelevantContent();
        assertTrue(content.contains("public int doCalculation(String strInput, int intInput)"));
        assertTrue(content.contains("if (strInput.equals(\"a\"))"));
        assertTrue(content.contains("result += 1;"));
        assertTrue(content.contains("if (strInput.equals(\"b\"))"));
        assertTrue(content.contains("result -= 5;"));
    }
}
