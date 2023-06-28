package com.muromuro.muromuro02.model;

/**
 * Models the user input for a MuroMuro solution, including
 * the multiple sections of the user input.
 */
public class UserInput {

    private String callerCode;
    private String mainDefinition;

    public UserInput() {
        this("", "");
    }

    public UserInput(String callerCode, String mainDefinition) {
        this.callerCode = callerCode;
        this.mainDefinition = mainDefinition;
    }

    public boolean isEmpty() {
        return callerCode.isEmpty() && mainDefinition.isEmpty();
    }

    public String getCallerCode() {
        return callerCode;
    }

    public void setCallerCode(String callerCode) {
        this.callerCode = callerCode;
    }

    public String getMainDefinition() {
        return mainDefinition;
    }

    public void setMainDefinition(String mainDefinition) {
        this.mainDefinition = mainDefinition;
    }
}
