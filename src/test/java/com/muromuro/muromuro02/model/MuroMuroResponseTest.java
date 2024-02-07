package com.muromuro.muromuro02.model;

import org.junit.jupiter.api.Test;

import static com.muromuro.muromuro02.model.MuroMuroResponse.combineResponses;
import static org.junit.jupiter.api.Assertions.assertEquals;

/** Runs unit tests for the MuroMuroResponse class. */
public class MuroMuroResponseTest {

    @Test
    public void testCombineResponses() {
        MuroMuroResponse response1 = new MuroMuroResponse(MuroMuroResponse.Status.SUCCESS);
        MuroMuroResponse response2 = new MuroMuroResponse(MuroMuroResponse.Status.FAILURE, "Error");
        MuroMuroResponse response3 = new MuroMuroResponse(MuroMuroResponse.Status.TIMEOUT, "Timeout");
        MuroMuroResponse response4 = new MuroMuroResponse(MuroMuroResponse.Status.SUCCESS);

        assertEquals(
                MuroMuroResponse.Status.SUCCESS,
                combineResponses(response1, response4).getStatus());
        assertEquals(
                MuroMuroResponse.Status.FAILURE,
                combineResponses(response1, response2, response3).getStatus());
        assertEquals(
                "Error",
                combineResponses(response1, response2, response3).getMessage());
        assertEquals(
                MuroMuroResponse.Status.TIMEOUT,
                combineResponses(response1, response4, response3).getStatus());
        assertEquals(
                "Timeout",
                combineResponses(response1, response4, response3).getMessage());
    }
}
