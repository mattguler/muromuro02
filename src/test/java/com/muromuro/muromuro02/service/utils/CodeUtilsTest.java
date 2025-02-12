package com.muromuro.muromuro02.service.utils;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Unit tests for the CodeUtils class. */
public class CodeUtilsTest {

    @Test
    public void testReplaceEnumNames_singleEnum_success() {
        String oldString = "public enum Blahblah {BLAH1, BLAH2}";
        String newString =
                CodeUtils.replaceEnumNames(oldString, "AccountState");
        assertEquals("public enum AccountState {BLAH1, BLAH2}", newString);
    }

    @Test
    public void testReplaceEnumNames_multiLineSingleEnum_success() {
        String oldString =
                "public enum Blahblah\n"
                        + "{BLAH1, BLAH2}";
        String newString =
                CodeUtils.replaceEnumNames(oldString, "AccountState");
        assertEquals(
                "public enum AccountState\n"
                        + "{BLAH1, BLAH2}", newString);
    }

    @Test
    public void testReplaceEnumNames_multiEnums_success() {
        String oldString =
                "public enum Blahblah {BLAH1, BLAH2}\n"
                        + "public enum Something{THING1, THING2}";
        String newString =
                CodeUtils.replaceEnumNames(oldString, "AccountState");
        assertEquals(
                "public enum AccountState {BLAH1, BLAH2}\n"
                        + "public enum AccountState{THING1, THING2}",
                newString);
    }

    @Test
    public void testReplaceEnumNames_multiLineMultiEnums_success() {
        String oldString =
                "public enum Blahblah\n"
                        + "  {BLAH1, BLAH2}\n"
                        + "public enum Something{\n"
                        + "  THING1, THING2}";
        String newString =
                CodeUtils.replaceEnumNames(oldString, "AccountState");
        assertEquals(
                "public enum AccountState\n"
                        + "  {BLAH1, BLAH2}\n"
                        + "public enum AccountState{\n"
                        + "  THING1, THING2}",
                newString);
    }

    @Test
    public void testReplaceEnumNames_failure() {
        String oldString = "boolean isActive = false;";
        String newString =
                CodeUtils.replaceEnumNames(oldString, "AccountState");
        assertEquals("boolean isActive = false;", newString);
    }

    @Test
    public void testReplaceEnumNames_empty() {
        String oldString = "";
        String newString =
                CodeUtils.replaceEnumNames(oldString, "AccountState");
        assertEquals("", newString);
    }

    @Test
    public void testReplaceEnumNames_badlyFormed_failure() {
        String oldString = "public enum Blah Blah {BLAH1, BLAH2}";
        String newString =
                CodeUtils.replaceEnumNames(oldString, "AccountState");
        assertEquals("public enum Blah Blah {BLAH1, BLAH2}", newString);
    }

    @Test
    public void testReplaceEnumNames_multiLineBadlyFormed() {
        String oldString =
                "public enum Blah Blah {BLAH1, BLAH2}\n"
                        + "public enum Something {THING1, THING2}";
        String newString =
                CodeUtils.replaceEnumNames(oldString, "AccountState");
        assertEquals(
                "public enum Blah Blah {BLAH1, BLAH2}\n"
                        + "public enum AccountState {THING1, THING2}",
                newString);
    }

    @Test
    public void testReplaceEnumNames_multiLineWithComments() {
        String oldString =
                "// This enum is about something.\n"
                        + "public enum Something {THING1, THING2}";
        String newString =
                CodeUtils.replaceEnumNames(oldString, "AccountState");
        assertEquals(
                "// This enum is about something.\n"
                        + "public enum AccountState {THING1, THING2}",
                newString);
    }

    @Test
    public void testReplaceEnumNames_justWithComments() {
        String oldString =
                "// There is no real enum here. Just this comment.";
        String newString =
                CodeUtils.replaceEnumNames(oldString, "AccountState");
        assertEquals(oldString, newString);
    }

    @Test
    public void testReplaceTargetWords_success() {
        String oldString = "public enum Blahblah {Blah1, Blah2, blah3}";
        String newString =
                CodeUtils.replaceTargetWords(oldString, "BLAH1", "blah1", "Blah1");
        newString =
                CodeUtils.replaceTargetWords(newString, "BLAH2", "blah2", "Blah2");
        newString =
                CodeUtils.replaceTargetWords(newString, "BLAH3", "blah3", "Blah3");
        assertEquals("public enum Blahblah {BLAH1, BLAH2, BLAH3}", newString);
    }

    @Test
    public void testReplaceTargetWords_success_2() {
        String oldString =
                "enum SomeEnumName {\n" +
                        "  Active,\n" +
                        "  Inactive,\n" +
                        "  Deleted,\n" +
                        "  Suspended\n" +
                        "}";
        String newString =
                CodeUtils.replaceTargetWords(oldString, "INACTIVE", "Inactive", "inactive");
        newString =
                CodeUtils.replaceTargetWords(newString, "ACTIVE", "Active", "Active");
        newString =
                CodeUtils.replaceTargetWords(newString, "SUSPENDED", "Suspended", "suspended");
        newString =
                CodeUtils.replaceTargetWords(newString, "DELETED", "Deleted", "deleted");
        assertEquals(
                "enum SomeEnumName {\n" +
                        "  ACTIVE,\n" +
                        "  INACTIVE,\n" +
                        "  DELETED,\n" +
                        "  SUSPENDED\n" +
                        "}",
                newString);
    }

    @Test
    public void testGetClassName_success() {
        String code = "public class SomeClass {};";
        String className = CodeUtils.getClassName(code);
        assertEquals("SomeClass", className);
    }

    @Test
    public void testGetClassName_empty() {
        String code = "";
        String className = CodeUtils.getClassName(code);
        assertNull(className);
    }

    @Test
    public void testGetClassName_failure1() {
        String code = "// There is nothing to see here.";
        String className = CodeUtils.getClassName(code);
        assertNull(className);
    }

    @Test
    public void testGetClassName_failure2() {
        String code = "// There is no actual class in this code string.";
        String className = CodeUtils.getClassName(code);
        assertNull(className);
    }

    @Test
    public void testGetClassName_withComments() {
        String code =
                "// There is the word class in this comment.\n"
                        + "public class SomeClass {};";
        String className = CodeUtils.getClassName(code);
        assertEquals("SomeClass", className);
    }

    @Test
    public void testGetClassName_withInnerClass() {
        String code =
                "// There is the word class in this comment.\n"
                        + "public class SomeClass {\n"
                        + "    public static class InnerClass {}\n"
                        + "}";
        // Should return the outer class only.
        String className = CodeUtils.getClassName(code);
        assertEquals("SomeClass", className);
    }

    @Test
    public void testValidateNotStartsWithImports_noImport() {
        String code = "public class SomeClass {}";
        MuroMuroResponse response = CodeUtils.validateNotStartsWithImports(code);
        assertEquals(MuroMuroResponse.Status.SUCCESS, response.getStatus());
        assertTrue(response.getMessage().isEmpty());
    }

    @Test
    public void testValidateNotStartsWithImports_withImport() {
        String code =
                "\n    \nimport java.util.*;\n"
                        + "public class SomeClass {}";
        MuroMuroResponse response = CodeUtils.validateNotStartsWithImports(code);
        assertEquals(MuroMuroResponse.Status.FAILURE, response.getStatus());
        assertEquals(
                "The code should not contain any import statements.",
                response.getMessage());
    }

    @Test
    public void testValidateNotStartsWithImports_empty() {
        String code = "";
        MuroMuroResponse response = CodeUtils.validateNotStartsWithImports(code);
        assertEquals(MuroMuroResponse.Status.SUCCESS, response.getStatus());
        assertTrue(response.getMessage().isEmpty());
    }

    @Test
    public void testCountKeywordOccurrences_empty() {
        String code = "";
        int count = CodeUtils.countKeywordOccurrences(code, "static");
        assertEquals(0, count);
    }

    @Test
    public void testCountKeywordOccurrences_noKeyword() {
        String code = "public class SomeClass {}";
        int count = CodeUtils.countKeywordOccurrences(code, "static");
        assertEquals(0, count);
    }

    @Test
    public void testCountKeywordOccurrences_singleKeyword() {
        String code = "public static class SomeClass {}";
        int count = CodeUtils.countKeywordOccurrences(code, "static");
        assertEquals(1, count);
    }

    @Test
    public void testCountKeywordOccurrences_multipleKeywords() {
        String code = "public static class SomeClass { static int someField; }";
        int count = CodeUtils.countKeywordOccurrences(code, "static");
        assertEquals(2, count);
    }
}
