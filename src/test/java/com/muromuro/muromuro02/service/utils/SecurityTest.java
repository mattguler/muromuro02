package com.muromuro.muromuro02.service.utils;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Tests the methods in the Security class. */
public class SecurityTest {
    private static final String EXAMPLE_JAVA_CODE_STR_FORMAT =
            "public class Example { \n"
                    + "    public static void main(String[] args) { \n"
                    + "        %s \n"
                    + "    } \n"
                    + "} \n";

    @Test
    public void testValidateCodeLength_fails() {
        int maxLength = 1000;
        String userInput = buildStringOfLength(maxLength + 1);
        MuroMuroResponse response = Security.validateCodeLength(userInput, maxLength);
        assertEquals(MuroMuroResponse.Status.FAILURE, response.getStatus());
        assertEquals(
                "The solution seems insecure, with length 1001 exceeding the max allowable length.",
                response.getErrorMessage());
    }

    @Test
    public void testValidateCodeLength_passes() {
        int maxLength = 1000;
        String userInput = buildStringOfLength(maxLength);
        MuroMuroResponse response = Security.validateCodeLength(userInput, maxLength);
        assertEquals(MuroMuroResponse.Status.SUCCESS, response.getStatus());
    }

    @Test
    public void testCheckIfCodeSecure() {
        validateCodeSecure("System.exit(0);", false);
        validateCodeSecure("System.out.println(\"Hello World!\");", false);
        validateCodeSecure("Runtime.getRuntime().exec(\"rm -rf /\")", false);
        validateCodeSecure("ProcessBuilder().command(\"rm\", \"-rf\", \"/\").start()", false);
        validateCodeSecure("Thread.currentThread().sleep(1000);", false);
        validateCodeSecure("SecurityManager.getClass()", false);
        validateCodeSecure("ClassLoader", false);
        validateCodeSecure(
                "Class.forName(\"java.lang.Something\").getMethod(\"exit\", int.class).invoke(null, 0);",
                false);

        validateCodeSecure("int i = 0;", true);
        validateCodeSecure("int i = 5; i++;", true);
    }

    private String buildStringOfLength(int length) {
        return Stream.generate(() -> "a")
                .limit(length)
                .collect(Collectors.joining());
    }

    private String buildExampleJavaCode(String codeWithKeyword) {
        return String.format(EXAMPLE_JAVA_CODE_STR_FORMAT, codeWithKeyword);
    }

    private void validateCodeSecure(String codeWithKeyword, boolean expectedResult) {
        String exampleJavaCode = buildExampleJavaCode(codeWithKeyword);
        MuroMuroResponse response = Security.checkIfCodeSecure(exampleJavaCode);
        if (expectedResult) {
            assertEquals(MuroMuroResponse.Status.SUCCESS, response.getStatus());
        }
        else {
            assertEquals(MuroMuroResponse.Status.FAILURE, response.getStatus());
        }
    }
}
