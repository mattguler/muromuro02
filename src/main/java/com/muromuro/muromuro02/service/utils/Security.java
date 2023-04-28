package com.muromuro.muromuro02.service.utils;

public class Security {

    public static class Response {
        boolean isSecure;
        String problemWord;

        public Response(boolean isSecure, String problemWord) {
            this.isSecure = isSecure;
            this.problemWord = problemWord;
        }

        public boolean isSecure() {
            return isSecure;
        }

        public String getProblemWord() {
            return problemWord;
        }
    }

    /**
     * Checks if the given user input is secure. Returns true if secure, false otherwise.
     * The user input is an arbitrary Java code snippet. It is meant to be run on a Docker container.
     * The code snippet is considered secure if it does not contain any of the following:
     * 1. System
     * 2. Runtime
     * 3. ProcessBuilder
     * 4. Process
     * 5. Thread
     * 6. ThreadGroup
     * 7. ThreadLocal
     * 8. SecurityManager
     * 9. ClassLoader
     * 10. Class.forName
     */
    public static Response isCodeSecure(String userInput) {
        String[] targetWords = {
                "System",
//                "System.exit",
                "Runtime",
//                "Runtime.getRuntime().exit",
//                "System.load",
//                "System.loadLibrary",
//                "Runtime.getRuntime().load",
//                "Runtime.getRuntime().loadLibrary",
//                "ProcessBuilder",
                "Process",
                "Thread",
//                "ThreadGroup",
//                "ThreadLocal",
                "SecurityManager",
                "ClassLoader",
                "Class.forName"
        };
        for (String target : targetWords) {
            if (userInput.contains(target)) {
                return new Response(false, target);
            }
        }
        return new Response(true, null);
    }
}
