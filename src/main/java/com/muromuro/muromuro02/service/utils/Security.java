package com.muromuro.muromuro02.service.utils;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import com.muromuro.muromuro02.model.UserInput;

/**
 * The Security class which is used to validate the security of the user's solution.
 */
public class Security {

    private static final String SYSTEM_KEYWORD = "System";

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
        if (containsSystemCalls(userInput.getCallerCode())
                || containsSystemCalls(userInput.getMainDefinition())) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.FAILURE,
                    String.format(
                            "The solution seems insecure, with forbidden keyword: %s",
                            SYSTEM_KEYWORD));
        }
        return new MuroMuroResponse(MuroMuroResponse.Status.SUCCESS);
    }

    /**
     * Returns true if the System keyword exists in the input string,
     * false otherwise. However, also returns false if it is a System.out.print or
     * a System.err.print call.
     */
    private static boolean containsSystemCalls(String input) {
        int index = input.indexOf(SYSTEM_KEYWORD);
        while (index >= 0) {
            if (!input.startsWith("System.out.print", index)
                    && !input.startsWith("System.err.print", index)) {
                return true;
            }
            index = input.indexOf(SYSTEM_KEYWORD, index + 1);
        }
        return false;
    }
}
