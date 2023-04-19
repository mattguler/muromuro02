package com.muromuro.muromuro02.model;

public class MuroMuroSolution {

    private String userInput;
    private MuroMuroResponse response;

    public MuroMuroSolution() {
        userInput = "";
        response = new MuroMuroResponse(MuroMuroResponse.Status.UNDEFINED);
    }

    public String getUserInput() {
        return userInput;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }

    public MuroMuroResponse getResponse() {
        return response;
    }

    public void setResponse(MuroMuroResponse response) {
        this.response = response;
    }
}
