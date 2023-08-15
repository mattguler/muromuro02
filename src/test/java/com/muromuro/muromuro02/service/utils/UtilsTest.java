package com.muromuro.muromuro02.service.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Unit tests the methods in the Utils class. */
public class UtilsTest {

    @Test
    public void testReplaceEnumNames_singleEnum_success() {
        String oldString = "public enum Blahblah {BLAH1, BLAH2}";
        String newString =
                Utils.replaceEnumNames(oldString, "AccountState");
        assertEquals("public enum AccountState {BLAH1, BLAH2}", newString);
    }

    @Test
    public void testReplaceEnumNames_multiLineSingleEnum_success() {
        String oldString =
                "public enum Blahblah\n"
                        + "{BLAH1, BLAH2}";
        String newString =
                Utils.replaceEnumNames(oldString, "AccountState");
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
                Utils.replaceEnumNames(oldString, "AccountState");
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
                Utils.replaceEnumNames(oldString, "AccountState");
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
                Utils.replaceEnumNames(oldString, "AccountState");
        assertEquals("boolean isActive = false;", newString);
    }

    @Test
    public void testReplaceEnumNames_empty() {
        String oldString = "";
        String newString =
                Utils.replaceEnumNames(oldString, "AccountState");
        assertEquals("", newString);
    }

    @Test
    public void testReplaceEnumNames_badlyFormed_failure() {
        String oldString = "public enum Blah Blah {BLAH1, BLAH2}";
        String newString =
                Utils.replaceEnumNames(oldString, "AccountState");
        assertEquals("public enum Blah Blah {BLAH1, BLAH2}", newString);
    }

    @Test
    public void testReplaceEnumNames_multiLineBadlyFormed() {
        String oldString =
                "public enum Blah Blah {BLAH1, BLAH2}\n"
                        + "public enum Something {THING1, THING2}";
        String newString =
                Utils.replaceEnumNames(oldString, "AccountState");
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
                Utils.replaceEnumNames(oldString, "AccountState");
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
                Utils.replaceEnumNames(oldString, "AccountState");
        assertEquals(oldString, newString);
    }

    @Test
    public void testReplaceTargetWords_success() {
        String oldString = "public enum Blahblah {Blah1, Blah2, blah3}";
        String newString =
                Utils.replaceTargetWords(oldString, "BLAH1", "blah1", "Blah1");
        newString =
                Utils.replaceTargetWords(newString, "BLAH2", "blah2", "Blah2");
        newString =
                Utils.replaceTargetWords(newString, "BLAH3", "blah3", "Blah3");
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
                Utils.replaceTargetWords(oldString, "INACTIVE", "Inactive", "inactive");
        newString =
                Utils.replaceTargetWords(newString, "ACTIVE", "Active", "Active");
        newString =
                Utils.replaceTargetWords(newString, "SUSPENDED", "Suspended", "suspended");
        newString =
                Utils.replaceTargetWords(newString, "DELETED", "Deleted", "deleted");
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
        String className = Utils.getClassName(code);
        assertEquals("SomeClass", className);
    }

    @Test
    public void testGetClassName_empty() {
        String code = "";
        String className = Utils.getClassName(code);
        assertEquals(null, className);
    }

    @Test
    public void testGetClassName_failure1() {
        String code = "// There is nothing to see here.";
        String className = Utils.getClassName(code);
        assertEquals(null, className);
    }

    @Test
    public void testGetClassName_failure2() {
        String code = "// There is no actual class in this code string.";
        String className = Utils.getClassName(code);
        assertEquals(null, className);
    }

    @Test
    public void testGetClassName_withComments() {
        String code =
                "// There is the word class in this comment.\n"
                        + "public class SomeClass {};";
        String className = Utils.getClassName(code);
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
        String className = Utils.getClassName(code);
        assertEquals("SomeClass", className);
    }

    @Test
    public void testPrependStaticIfMissing_empty() {
        String code = "";
        String newCode = Utils.prependStaticIfMissing(code);
        assertEquals("", newCode);
    }

    @Test
    public void testPrependStaticIfMissing_success() {
        String code = "public void someMethod() {}";
        String newCode = Utils.prependStaticIfMissing(code);
        assertEquals("static public void someMethod() {}", newCode);
    }

    @Test
    public void testPrependStaticIfMissing_success2() {
        String code = "void someMethod();";
        String newCode = Utils.prependStaticIfMissing(code);
        assertEquals("static void someMethod();", newCode);
    }

    @Test
    public void testPrependStaticIfMissing_noReplacement() {
        String code = "public static void someMethod();";
        String newCode = Utils.prependStaticIfMissing(code);
        assertEquals(code, newCode);
    }
}
