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
            "public List<Employee> getAllEmployees(DatabaseProxy dbProxy) { return null; }";
    private static final String DEVICE_DATABASE_CALLER_CODE =
            "List<String> resultDevicesInDdb = findDevicesInDdb(inputDevices, ddb);";
    private static final String DEVICE_DATABASE_MAIN_DEFINITION =
            "public List<String> findDevicesInDdb(List<String> inputDevices, DeviceDatabase ddb) "
                    + "{ return null; }";
    private static final String REFACTOR_TOO_MANY_IFS_MAIN_DEFINITION =
            "public int doCalculation(String strInput, int intInput) { return 0; }";
    private static final String REPRESENT_ACCOUNT_STATES_MAIN_DEFINITION =
            "public enum SomeAccountState {}";

    @Autowired
    @Qualifier("designApiWithPagination")
    private EvaluationCode designApiWithPaginationEval;

    @Autowired
    @Qualifier("deviceDatabase")
    private EvaluationCode deviceDatabaseEval;

    @Autowired
    @Qualifier("refactorTooManyIfs")
    private EvaluationCode refactorTooManyIfsEval;

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
    public void testGetContent_deviceDatabaseEval() {
        String content = deviceDatabaseEval.getFormattedContent();
        assertTrue(content.contains("public class DeviceDatabaseEval"));
        assertTrue(content.contains("interface DeviceDatabase"));
        assertTrue(content.contains("public void callerFunction("));
        assertTrue(content.contains("public static void main(String[] args)"));
    }

    @Test
    public void testReplaceCallerCode_deviceDatabaseEval() {
        String content = deviceDatabaseEval
                .replaceCallerCode(DEVICE_DATABASE_CALLER_CODE)
                .getFormattedContent();
        assertTrue(content.contains("public class DeviceDatabaseEval"));
        assertTrue(content.contains("interface DeviceDatabase"));
        assertTrue(content.contains(DEVICE_DATABASE_CALLER_CODE));
        assertTrue(content.contains("public static void main(String[] args)"));
    }

    @Test
    public void testReplaceMainDefinition_deviceDatabaseEval() {
        String content = deviceDatabaseEval
                .replaceMainDefinition(DEVICE_DATABASE_MAIN_DEFINITION)
                .getFormattedContent();
        assertTrue(content.contains("public class DeviceDatabaseEval"));
        assertTrue(content.contains("interface DeviceDatabase"));
        assertTrue(content.contains(DEVICE_DATABASE_MAIN_DEFINITION));
        assertTrue(content.contains("public static void main(String[] args)"));
    }

    @Test
    public void testReplaceAll_deviceDatabaseEval() {
        String content = deviceDatabaseEval
                .replaceCallerCode(DEVICE_DATABASE_CALLER_CODE)
                .replaceMainDefinition(DEVICE_DATABASE_MAIN_DEFINITION)
                .getFormattedContent();
        assertTrue(content.contains("public class DeviceDatabaseEval"));
        assertTrue(content.contains("interface DeviceDatabase"));
        assertTrue(content.contains(DEVICE_DATABASE_CALLER_CODE));
        assertTrue(content.contains(DEVICE_DATABASE_MAIN_DEFINITION));
        assertTrue(content.contains("public static void main(String[] args)"));
    }

    @Test
    public void testGetContent_refactorTooManyIfsEval() {
        String content = refactorTooManyIfsEval.getFormattedContent();
        assertTrue(content.contains("public class RefactorTooManyIfsEval"));
        assertTrue(content.contains("public int doCalculation(String strInput, int intInput)"));
        assertTrue(content.contains("validateCalculation(\\\"a\\\", intInput, 16)"));
        assertTrue(content.contains("validateCalculation(\\\"b\\\", intInput, 10)"));
        assertTrue(content.contains("public void doValidation1()"));
        assertTrue(content.contains("public void doValidation2()"));
        assertTrue(content.contains("private boolean validateCalculation("));
        assertTrue(content.contains("public static void main(String[] args)"));
    }

    @Test
    public void testReplaceMainDefinition_refactorTooManyIfsEval() {
        String content = refactorTooManyIfsEval
                .replaceMainDefinition(REFACTOR_TOO_MANY_IFS_MAIN_DEFINITION)
                .getFormattedContent();
        assertTrue(content.contains("public class RefactorTooManyIfsEval"));
        assertTrue(content.contains(REFACTOR_TOO_MANY_IFS_MAIN_DEFINITION));
        assertTrue(content.contains("validateCalculation(\\\"a\\\", intInput, 16)"));
        assertTrue(content.contains("validateCalculation(\\\"b\\\", intInput, 10)"));
        assertTrue(content.contains("public void doValidation1()"));
        assertTrue(content.contains("public static void main(String[] args)"));
    }

    @Test
    public void testReplaceCallerCode_refactorTooManyIfsEval() {
        Exception thrown = assertThrows(
                IllegalArgumentException.class,
                () -> {
                    refactorTooManyIfsEval.replaceCallerCode("//");
                });
        assertTrue(thrown.getMessage().contains("Could not find the beginning or end sequence."));
    }

    @Test
    public void testGetContent_representAccountStatesEval() {
        String content = representAccountStatesEval.getFormattedContent();
        assertTrue(content.contains("System.out.println(\\\"Account state ACTIVE.\\\");"));
        assertTrue(content.contains("public void evalAccountState(AccountState accountState)"));
        assertTrue(content.contains("public static void main(String[] args)"));
    }

    @Test
    public void testReplaceMainDefinition_representAccountStatesEval() {
        String content = representAccountStatesEval
                .replaceMainDefinition(REPRESENT_ACCOUNT_STATES_MAIN_DEFINITION)
                .getFormattedContent();
        assertTrue(content.contains(REPRESENT_ACCOUNT_STATES_MAIN_DEFINITION));
        assertTrue(content.contains("public void evalAccountState(AccountState accountState)"));
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
