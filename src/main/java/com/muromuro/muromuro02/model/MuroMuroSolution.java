package com.muromuro.muromuro02.model;

/**
 * Models the solution for a MuroMuro question, which includes the user's input and
 * the server-side evaluation of the solution.
 * TODO: Maybe remove this and use each of its pieces individually in the Thymeleaf
 *       pages and in the Spring Boot Controllers.
 */
public class MuroMuroSolution {

    // TODO: Rename this to initialInput for better clarity, after the
    //       CodeMirror migration is completed.
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
