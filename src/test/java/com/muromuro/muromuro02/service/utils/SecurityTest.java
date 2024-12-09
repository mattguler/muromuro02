package com.muromuro.muromuro02.service.utils;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import com.muromuro.muromuro02.model.UserInput;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.muromuro.muromuro02.service.utils.Security.Options.ENABLE_FILE_IO_SUPPORT;
import static com.muromuro.muromuro02.service.utils.Security.Options.ENABLE_MULTI_THREAD_SUPPORT;
import static org.junit.jupiter.api.Assertions.assertEquals;

/** Unit tests the methods in the Security class. */
public class SecurityTest {
    private static final String EXAMPLE_JAVA_CODE_STR_FORMAT =
            "public class Example { \n"
                    + "    public static void main(String[] args) { \n"
                    + "        %s \n"
                    + "    } \n"
                    + "} \n";

    @Test
    public void testValidateCallerCodeLength_fails() {
        int maxLength = 1000;
        UserInput userInput =
                new UserInput(buildStringOfLength(maxLength + 1), "");
        MuroMuroResponse response = Security.validateCodeLength(userInput, maxLength);
        assertEquals(MuroMuroResponse.Status.FAILURE, response.getStatus());
        assertEquals(
                "The solution seems insecure, with its length exceeding the max allowable length.",
                response.getMessage());
    }

    @Test
    public void testValidateCallerCodeLength_passes() {
        int maxLength = 1000;
        UserInput userInput = new UserInput(buildStringOfLength(maxLength), "");
        MuroMuroResponse response = Security.validateCodeLength(userInput, maxLength);
        assertEquals(MuroMuroResponse.Status.SUCCESS, response.getStatus());
    }

    @Test
    public void testValidateMainDefinitionLength_fails() {
        int maxLength = 1000;
        UserInput userInput =
                new UserInput("", buildStringOfLength(maxLength + 1));
        MuroMuroResponse response = Security.validateCodeLength(userInput, maxLength);
        assertEquals(MuroMuroResponse.Status.FAILURE, response.getStatus());
        assertEquals(
                "The solution seems insecure, with its length exceeding the max allowable length.",
                response.getMessage());
    }

    @Test
    public void testValidateMainDefinitionLength_passes() {
        int maxLength = 1000;
        UserInput userInput = new UserInput("", buildStringOfLength(maxLength));
        MuroMuroResponse response = Security.validateCodeLength(userInput, maxLength);
        assertEquals(MuroMuroResponse.Status.SUCCESS, response.getStatus());
    }

    @Test
    public void testCheckIfCodeSecure() {
        validateCallerCodeSecure("System.exit(0);", false);
        validateMainDefinitionSecure("System.out.println(\"Hello World!\");", true);
        validateCallerCodeSecure("//Some comment first\n System.exit(0);", false);
        validateMainDefinitionSecure(
                "//Some comment first\n System.out.println(\"Hello World!\");", true);
        validateCallerCodeSecure(
                "//Some comment first\n"
                        + "System.err.println(\"Hello World!\");\n"
                        + "System.exit(0);",
                false);
        validateMainDefinitionSecure(
                "//Some comment first\n"
                        + "System.out.println(\"Hello there!\");\n"
                        + "System.err.println(\"General Kenobi!\");\n",
                true);
        validateCallerCodeSecure("Runtime.getRuntime().exec(\"rm -rf /\")", false);
        validateMainDefinitionSecure(
                "ProcessBuilder().command(\"rm\", \"-rf\", \"/\").start()", false);
        validateCallerCodeSecure("Process process;", false);
        validateMainDefinitionSecure("Processor processor1;", true);
        validateCallerCodeSecure("runProcess();", true);
        validateMainDefinitionSecure("int a = 5;\nProcess process;", false);
        validateCallerCodeSecure("int a = 5;\nProcessor processor1;", true);
        validateMainDefinitionSecure("int a = 5;\nrunProcess();", true);
        validateCallerCodeSecure("Thread.currentThread().sleep(1000);", false);
        validateMainDefinitionSecure(
                "java.util.concurrent.ExecutorService pool;", false);
        validateCallerCodeSecure(
                "java.util.concurrent.ExecutorService pool;\n"
                        + "Thread.currentThread().sleep(1000);",
                true,
                ENABLE_MULTI_THREAD_SUPPORT);
        validateMainDefinitionSecure("SecurityManager.getClass()", false);
        validateCallerCodeSecure("ClassLoader", false);
        validateMainDefinitionSecure(
                "Class.forName(\"java.lang.Something\").getMethod(\"exit\", int.class).invoke(null, 0);",
                false);
        validateCallerCodeSecure(
                "java.io.File f = new File(\"some_system_file\");", false);
        validateMainDefinitionSecure(
                "java.io.File f = new File(\"some_system_file\");",
                true,
                ENABLE_FILE_IO_SUPPORT);
        validateMainDefinitionSecure(
                "java.util.concurrent.ExecutorService pool;\n"
                        + "Thread.currentThread().sleep(1000);"
                        + "java.io.File f = new File(\"some_system_file\");",
                true,
                ENABLE_MULTI_THREAD_SUPPORT,
                ENABLE_FILE_IO_SUPPORT);

        validateCallerCodeSecure("int i = 0;", true);
        validateMainDefinitionSecure("int i = 5; i++;", true);
    }

    private String buildStringOfLength(int length) {
        return Stream.generate(() -> "a")
                .limit(length)
                .collect(Collectors.joining());
    }

    private String buildExampleJavaCode(String codeWithKeyword) {
        return String.format(EXAMPLE_JAVA_CODE_STR_FORMAT, codeWithKeyword);
    }

    private void validateCallerCodeSecure(
            String codeWithKeyword, boolean expectedResult, Security.Options... options) {
        UserInput userInput =
                new UserInput(buildExampleJavaCode(codeWithKeyword), "");
        MuroMuroResponse response = Security.checkIfCodeSecure(userInput, options);
        if (expectedResult) {
            assertEquals(MuroMuroResponse.Status.SUCCESS, response.getStatus());
        }
        else {
            assertEquals(MuroMuroResponse.Status.FAILURE, response.getStatus());
        }
    }

    private void validateMainDefinitionSecure(
            String codeWithKeyword, boolean expectedResult, Security.Options... options) {
        UserInput userInput =
                new UserInput("", buildExampleJavaCode(codeWithKeyword));
        MuroMuroResponse response = Security.checkIfCodeSecure(userInput, options);
        if (expectedResult) {
            assertEquals(MuroMuroResponse.Status.SUCCESS, response.getStatus());
        }
        else {
            assertEquals(MuroMuroResponse.Status.FAILURE, response.getStatus());
        }
    }
}
