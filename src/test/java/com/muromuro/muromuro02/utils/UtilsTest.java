package com.muromuro.muromuro02.utils;

import com.muromuro.muromuro02.service.utils.Utils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
