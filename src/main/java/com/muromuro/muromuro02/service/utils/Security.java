package com.muromuro.muromuro02.service.utils;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import com.muromuro.muromuro02.model.UserInput;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * The Security class which is used to validate the security of the user's solution.
 */
@Service
public class Security {

    public enum Options {
        ENABLE_MULTI_THREAD_SUPPORT,
        ENABLE_FILE_IO_SUPPORT
    }

    private final String SYSTEM_KEYWORD = "System";
    private final String PROCESS_KEYWORD = "Process";

    /**
     * Validates if the given user input code is below a certain given length.
     * Returns a MuroMuroResponse object containing the status and the error message if applicable.
     */
    public MuroMuroResponse validateCodeLength(UserInput userInput, int maxLength) {
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
     * Pass in optional arguments to enable support for multithreading and/or file io operations.
     */
    public MuroMuroResponse checkIfCodeSecure(UserInput userInput, Options... options) {
        List<String> targetWords = getTargetWords(options);
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
        if (containsProcessCalls(userInput.getCallerCode())
                || containsProcessCalls(userInput.getMainDefinition())) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.FAILURE,
                    String.format(
                            "The solution seems insecure, with forbidden keyword: %s",
                            PROCESS_KEYWORD));
        }
        return new MuroMuroResponse(MuroMuroResponse.Status.SUCCESS);
    }

    private List<String> getTargetWords(Options[] options) {
        List<String> targetWords =
                new ArrayList<>(List.of(
                        "Runtime",
                        "SecurityManager",
                        "ClassLoader",
                        "Class.forName"));
        Set<Options> optionsSet = Set.of(options);
        if (!optionsSet.contains(Options.ENABLE_MULTI_THREAD_SUPPORT)) {
            targetWords.add("Thread");
            targetWords.add("java.util.concurrent");
        }
        if (!optionsSet.contains(Options.ENABLE_FILE_IO_SUPPORT)) {
            targetWords.add("java.io");
        }
        return targetWords;
    }

    /**
     * Returns true if the System keyword exists in the input string,
     * false otherwise. However, also returns false if it is a System.out.print or
     * a System.err.print call.
     */
    private boolean containsSystemCalls(String input) {
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

    /**
     * Returns true if the Process keyword exists in the input string,
     * false otherwise. However, also returns false if the word is "Processor"
     * or "runProcess".
     */
    private boolean containsProcessCalls(String input) {
        int index = input.indexOf(PROCESS_KEYWORD);
        while (index >= 0) {
            if (index < 3 && !input.startsWith("Processor", index)) {
                return true;
            }
            else if (!input.startsWith("Processor", index)
                    && !input.startsWith("runProcess", index - 3)) {
                return true;
            }
            index = input.indexOf(PROCESS_KEYWORD, index + 1);
        }
        return false;
    }
}
