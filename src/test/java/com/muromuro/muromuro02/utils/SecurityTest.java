package com.muromuro.muromuro02.utils;

import com.muromuro.muromuro02.service.utils.Security;
import org.junit.jupiter.api.Test;

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
    public void testIsCodeSecure() {
        testIsCodeSecureHelper("System.exit(0);", false);
        testIsCodeSecureHelper("System.out.println(\"Hello World!\");", false);
        testIsCodeSecureHelper("Runtime.getRuntime().exec(\"rm -rf /\")", false);
        testIsCodeSecureHelper("ProcessBuilder().command(\"rm\", \"-rf\", \"/\").start()", false);
        testIsCodeSecureHelper("Thread.currentThread().sleep(1000);", false);
        testIsCodeSecureHelper("SecurityManager.getClass()", false);
        testIsCodeSecureHelper("ClassLoader", false);
        testIsCodeSecureHelper(
                "Class.forName(\"java.lang.Something\").getMethod(\"exit\", int.class).invoke(null, 0);",
                false);

        testIsCodeSecureHelper("int i = 0;", true);
        testIsCodeSecureHelper("int i = 5; i++;", true);
    }

    private String buildExampleJavaCode(String codeWithKeyword) {
        return String.format(EXAMPLE_JAVA_CODE_STR_FORMAT, codeWithKeyword);
    }

    private void testIsCodeSecureHelper(String codeWithKeyword, boolean expectedResult) {
        String exampleJavaCode = buildExampleJavaCode(codeWithKeyword);
        Security.Response actualResult = Security.isCodeSecure(exampleJavaCode);
        assertEquals(expectedResult, actualResult.isSecure());
    }
}
