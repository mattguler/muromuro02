package com.muromuro.muromuro02.model;

public class MuroMuroResponse {

    public enum Status {
        UNDEFINED,
        SUCCESS,
        FAILURE,
        TIMEOUT
    }

    private final Status status;
    private final String errorMessage;

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

    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Combines the given MuroMuroResponse objects into a single MuroMuroResponse object.
     */
    public static MuroMuroResponse combineResponses(MuroMuroResponse... responses) {
        for (MuroMuroResponse response : responses) {
            if (response.getStatus() != MuroMuroResponse.Status.SUCCESS) {
                return response;
            }
        }
        return new MuroMuroResponse(MuroMuroResponse.Status.SUCCESS);
    }
}
