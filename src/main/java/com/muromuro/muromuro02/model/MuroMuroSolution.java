package com.muromuro.muromuro02.model;

/**
 * Models the solution for a MuroMuro question, which includes the user's input and
 * the server-side evaluation of the solution.
 * TODO: Maybe remove this and use each of its pieces individually in the Thymeleaf
 *       pages and in the Spring Boot Controllers.
 */
public class MuroMuroSolution {

    private UserInput initialInput;
    private MuroMuroResponse response;

    public MuroMuroSolution() {
        initialInput = new UserInput();
        response = new MuroMuroResponse(MuroMuroResponse.Status.UNDEFINED);
    }

    public UserInput getInitialInput() {
        return initialInput;
    }

    public void setInitialInput(UserInput initialInput) {
        this.initialInput = initialInput;
    }

    public MuroMuroResponse getResponse() {
        return response;
    }

    public void setResponse(MuroMuroResponse response) {
        this.response = response;
    }
}
