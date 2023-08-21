package com.muromuro.muromuro02.service.utils;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import com.muromuro.muromuro02.model.UserInput;

/**
 * The Security class which is used to validate the security of the user's solution.
 */
public class Security {

    /**
     * Validates if the given user input code is below a certain given length.
     * Returns a MuroMuroResponse object containing the status and the error message if applicable.
     */
    public static MuroMuroResponse validateCodeLength(UserInput userInput, int maxLength) {
        if (userInput.getCallerCode().length() > maxLength
                || userInput.getMainDefinition().length() > maxLength) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.FAILURE,
                    "The solution seems insecure, with its length exceeding the max allowable length.");
        }
        return new MuroMuroResponse(MuroMuroResponse.Status.SUCCESS);
    }

    /**
     * Checks if the given user input code is secure.
     * Returns a MuroMuroResponse object containing the status and the error message if applicable.
     * The user input is an arbitrary Java code snippet. It is meant to be run on a Docker container.
     * The code snippet is considered secure if it does not contain any specific target keyword.
     */
    public static MuroMuroResponse checkIfCodeSecure(UserInput userInput) {
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
            if (userInput.getCallerCode().contains(target)
                    || userInput.getMainDefinition().contains(target)) {
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
