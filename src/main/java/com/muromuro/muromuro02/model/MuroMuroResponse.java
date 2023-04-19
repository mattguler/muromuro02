package com.muromuro.muromuro02.model;

public class MuroMuroResponse {

    public enum Status {
        UNDEFINED,
        SUCCESS,
        FAILURE,
        TIMEOUT
    }

    private Status status;
    private String errorMessage;

    public MuroMuroResponse(Status status) {
        this(status, "");
    }

    public MuroMuroResponse(Status status, String errorMessage) {
        this.status = status;
        this.errorMessage = errorMessage;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
