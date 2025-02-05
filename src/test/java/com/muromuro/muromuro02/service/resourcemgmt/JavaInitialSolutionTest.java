package com.muromuro.muromuro02.service.resourcemgmt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the JavaInitialSolution object. This is an integration test because
 * it requires the Spring Boot context to configure the JavaInitialSolution bean.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class JavaInitialSolutionTest {

    @Autowired
    @Qualifier("debugListSoln")
    private JavaInitialSolution debugListSoln;

    @Autowired
    @Qualifier("duplicateRpcsSoln")
    private JavaInitialSolution duplicateRpcsSoln;

    @Autowired
    @Qualifier("incompatibleInterfacesSoln")
    private JavaInitialSolution incompatibleInterfacesSoln;

    @Autowired
    @Qualifier("longRunningFunctionsSoln")
    private JavaInitialSolution longRunningFunctionsSoln;

    @Autowired
    @Qualifier("refactorTooManyIfsSoln")
    private JavaInitialSolution refactorTooManyIfsSoln;

    @Test
    public void testGetContent_debugListSoln() {
        String content = debugListSoln.getMainDefinition();
        assertTrue(content.contains("List<Integer> createFiveElements()"));
        assertTrue(content.contains("void addFiveElements(List<Integer> series)"));
        assertTrue(content.contains("void removeFirstFiveElements(List<Integer> series)"));
    }

    @Test
    public void testGetContent_duplicateRpcsSoln() {
        String mainDef = duplicateRpcsSoln.getMainDefinition();
        assertTrue(mainDef.contains("public interface KVListService<K, V>"));
        assertTrue(mainDef.contains("public static final class KVListServiceImpl<K, V>"));
        String callerCode = duplicateRpcsSoln.getCallerCode();
        assertTrue(callerCode.contains("public static final class KVListClient<K, V>"));
    }

    @Test
    public void testGetContent_incompatibleInterfacesSoln() {
        String content = incompatibleInterfacesSoln.getMainDefinition();
        assertTrue(content.contains("private final Client client;"));
        assertTrue(content.contains("private final Processor2 processor2;"));
        assertTrue(content.contains("public int runProcess(int x)"));
        assertTrue(content.contains("return client.runProcess(processor1, x);"));
    }

    @Test
    public void testGetContent_longRunningFunctionsSoln() {
        String content = longRunningFunctionsSoln.getMainDefinition();
        assertTrue(content.contains("public boolean runAllBlackBoxes("));
        assertTrue(
                content.contains(
                        "acct.setValue(acct.getValue() + amount)"));
    }

    @Test
    public void testGetContent_refactorTooManyIfsSoln() {
        String content = refactorTooManyIfsSoln.getMainDefinition();
        assertTrue(content.contains("public int doCalculation(String strInput, int intInput)"));
        assertTrue(content.contains("if (strInput.equals(\"a\"))"));
        assertTrue(content.contains("result += 1;"));
        assertTrue(content.contains("if (strInput.equals(\"b\"))"));
        assertTrue(content.contains("result -= 5;"));
    }
}
