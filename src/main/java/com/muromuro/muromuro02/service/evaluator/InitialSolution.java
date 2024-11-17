package com.muromuro.muromuro02.service.evaluator;

/**
 * Represents the initial solution to a MuroMuro question.
 * Its content is read from a resource file, and then its relevant parts
 * are served to the user.
 */
public class InitialSolution {

        private static final String INITIAL_SOLUTION_START = "// Start initial solution implementation.";
        private static final String INITIAL_SOLUTION_END = "// End initial solution implementation.";

        private final String content;

        public InitialSolution(String content) {
            this.content = content;
        }

        /**
         * Returns the content of the initial solution between the INITIAL_SOLUTION_START
         * and INITIAL_SOLUTION_END strings.
         */
        public String getRelevantContent() {
            int beginningIndex = content.indexOf(INITIAL_SOLUTION_START);
            int endIndex = content.indexOf(INITIAL_SOLUTION_END);
            if (beginningIndex == -1 || endIndex == -1) {
                throw new IllegalArgumentException("Could not find the beginning or end sequence.");
            }
            return content.substring(beginningIndex + INITIAL_SOLUTION_START.length(), endIndex);
        }
}
