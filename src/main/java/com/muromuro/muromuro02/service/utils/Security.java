package com.muromuro.muromuro02.service.utils;

import com.muromuro.muromuro02.model.MuroMuroResponse;

/**
 * The Security class which is used to validate the security of the user's solution.
 */
public class Security {

    /**
     * Validates if the given user input code is below a certain given length.
     * Returns a MuroMuroResponse object containing the status and the error message if applicable.
     */
    public static MuroMuroResponse validateCodeLength(String userInput, int maxLength) {
        if (userInput.length() > maxLength) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.FAILURE,
                    String.format(
                            "The solution seems insecure, with length %d exceeding the max allowable length.",
                            userInput.length()));
        }
        return new MuroMuroResponse(MuroMuroResponse.Status.SUCCESS);
    }

    /**
     * Checks if the given user input code is secure.
     * Returns a MuroMuroResponse object containing the status and the error message if applicable.
     * The user input is an arbitrary Java code snippet. It is meant to be run on a Docker container.
     * The code snippet is considered secure if it does not contain any of the following:
     * 1. System
     * 2. Runtime
     * 3. Process
     * 4. Thread
     * 5. SecurityManager
     * 6. ClassLoader
     * 7. Class.forName
     */
    public static MuroMuroResponse checkIfCodeSecure(String userInput) {
        String[] targetWords = {
                "System",
                "Runtime",
                "Process",
                "Thread",
                "SecurityManager",
                "ClassLoader",
                "Class.forName"
        };
        for (String target : targetWords) {
            if (userInput.contains(target)) {
                return new MuroMuroResponse(
                        MuroMuroResponse.Status.FAILURE,
                        String.format(
                                "The solution seems insecure, with forbidden keyword: %s",
                                target));
            }
        }
        return new MuroMuroResponse(MuroMuroResponse.Status.SUCCESS);
    }
}
