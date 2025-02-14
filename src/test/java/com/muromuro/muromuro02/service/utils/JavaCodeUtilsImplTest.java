package com.muromuro.muromuro02.service.utils;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Unit tests for the CodeUtils class. */
public class JavaCodeUtilsImplTest {

    private CodeUtils codeUtils;

    @BeforeEach
    public void setUp() {
        codeUtils = new JavaCodeUtilsImpl();
    }

    @Test
    public void testReplaceEnumNames_singleEnum_success() {
        String oldString = "public enum Blahblah {BLAH1, BLAH2}";
        String newString =
                codeUtils.replaceEnumNames(oldString, "AccountState");
        assertEquals("public enum AccountState {BLAH1, BLAH2}", newString);
    }

    @Test
    public void testReplaceEnumNames_multiLineSingleEnum_success() {
        String oldString =
                "public enum Blahblah\n"
                        + "{BLAH1, BLAH2}";
        String newString =
                codeUtils.replaceEnumNames(oldString, "AccountState");
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
                codeUtils.replaceEnumNames(oldString, "AccountState");
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
                codeUtils.replaceEnumNames(oldString, "AccountState");
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
                codeUtils.replaceEnumNames(oldString, "AccountState");
        assertEquals("boolean isActive = false;", newString);
    }

    @Test
    public void testReplaceEnumNames_empty() {
        String oldString = "";
        String newString =
                codeUtils.replaceEnumNames(oldString, "AccountState");
        assertEquals("", newString);
    }

    @Test
    public void testReplaceEnumNames_badlyFormed_failure() {
        String oldString = "public enum Blah Blah {BLAH1, BLAH2}";
        String newString =
                codeUtils.replaceEnumNames(oldString, "AccountState");
        assertEquals("public enum Blah Blah {BLAH1, BLAH2}", newString);
    }

    @Test
    public void testReplaceEnumNames_multiLineBadlyFormed() {
        String oldString =
                "public enum Blah Blah {BLAH1, BLAH2}\n"
                        + "public enum Something {THING1, THING2}";
        String newString =
                codeUtils.replaceEnumNames(oldString, "AccountState");
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
                codeUtils.replaceEnumNames(oldString, "AccountState");
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
                codeUtils.replaceEnumNames(oldString, "AccountState");
        assertEquals(oldString, newString);
    }

    @Test
    public void testReplaceTargetWords_success() {
        String oldString = "public enum Blahblah {Blah1, Blah2, blah3}";
        String newString =
                codeUtils.replaceTargetWords(
                        oldString, "BLAH1", "blah1", "Blah1");
        newString =
                codeUtils.replaceTargetWords(
                        newString, "BLAH2", "blah2", "Blah2");
        newString =
                codeUtils.replaceTargetWords(
                        newString, "BLAH3", "blah3", "Blah3");
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
                codeUtils.replaceTargetWords(
                        oldString, "INACTIVE", "Inactive", "inactive");
        newString =
                codeUtils.replaceTargetWords(
                        newString, "ACTIVE", "Active", "Active");
        newString =
                codeUtils.replaceTargetWords(
                        newString, "SUSPENDED", "Suspended", "suspended");
        newString =
                codeUtils.replaceTargetWords(
                        newString, "DELETED", "Deleted", "deleted");
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
        String className = codeUtils.getClassName(code);
        assertEquals("SomeClass", className);
    }

    @Test
    public void testGetClassName_empty() {
        String code = "";
        String className = codeUtils.getClassName(code);
        assertNull(className);
    }

    @Test
    public void testGetClassName_failure1() {
        String code = "// There is nothing to see here.";
        String className = codeUtils.getClassName(code);
        assertNull(className);
    }

    @Test
    public void testGetClassName_failure2() {
        String code = "// There is no actual class in this code string.";
        String className = codeUtils.getClassName(code);
        assertNull(className);
    }

    @Test
    public void testGetClassName_withComments() {
        String code =
                """
                        // There is the word class in this comment.
                        public class SomeClass {};
                        """;
        String className = codeUtils.getClassName(code);
        assertEquals("SomeClass", className);
    }

    @Test
    public void testGetClassName_withInnerClass() {
        String code =
                """
                        // There is the word class in this comment.
                        public class SomeClass {
                            public static class InnerClass {}
                        }
                        """;
        // Should return the outer class only.
        String className = codeUtils.getClassName(code);
        assertEquals("SomeClass", className);
    }

    @Test
    public void testValidateNotStartsWithImports_success() {
        String code = "public class SomeClass {};";
        MuroMuroResponse response = codeUtils.validateNotStartsWithImports(code);
        assertEquals(MuroMuroResponse.Status.SUCCESS, response.getStatus());
    }

    @Test
    public void testValidateNotStartsWithImports_failure() {
        String code =
                """
                        import java.util.List;
                        public class SomeClass {}
                        """;
        MuroMuroResponse response = codeUtils.validateNotStartsWithImports(code);
        assertEquals(MuroMuroResponse.Status.FAILURE, response.getStatus());
    }

    @Test
    public void testCountKeywordOccurrences_success() {
        String code = "if (x > 0) { if (y < 0) { return; } }";
        int count = codeUtils.countKeywordOccurrences(code, "if");
        assertEquals(2, count);
    }

    @Test
    public void testCountKeywordOccurrences_noOccurrences() {
        String code = "while (true) { break; }";
        int count = codeUtils.countKeywordOccurrences(code, "if");
        assertEquals(0, count);
    }

    @Test
    public void testCountKeywordOccurrences_emptyString() {
        String code = "";
        int count = codeUtils.countKeywordOccurrences(code, "if");
        assertEquals(0, count);
    }
}
