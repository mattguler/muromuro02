package com.muromuro.muromuro02.model;

/**
 * Models the solution for a MuroMuro question, which includes the user's input and
 * the server-side evaluation of the solution.
 */
public class MuroMuroSolution {

    private UserInput userInput;
    private MuroMuroResponse response;

    public MuroMuroSolution() {
        userInput = new UserInput();
        response = new MuroMuroResponse(MuroMuroResponse.Status.UNDEFINED);
    }

    public UserInput getUserInput() {
        return userInput;
    }

    public void setUserInput(UserInput userInput) {
        this.userInput = userInput;
    }

    public MuroMuroResponse getResponse() {
        return response;
    }

    public void setResponse(MuroMuroResponse response) {
        this.response = response;
    }
}
