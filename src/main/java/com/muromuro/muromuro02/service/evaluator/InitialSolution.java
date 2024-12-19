package com.muromuro.muromuro02.service.evaluator;

/**
 * Represents the initial solution to a MuroMuro question.
 * Its content is read from a resource file, and then its relevant parts
 * are served to the user.
 */
public class InitialSolution {

        private static final String INITIAL_MAIN_DEF_START =
                "// Start initial main definition implementation.";
        private static final String INITIAL_MAIN_DEF_END =
                "// End initial main definition implementation.";

        private final String content;

        public InitialSolution(String content) {
            this.content = content;
        }

        /**
         * Returns the content of the initial main definition between the
         * INITIAL_MAIN_DEF_START and the INITIAL_MAIN_DEF_END strings.
         */
        public String getMainDefinition() {
            int beginningIndex = content.indexOf(INITIAL_MAIN_DEF_START);
            int endIndex = content.indexOf(INITIAL_MAIN_DEF_END);
            if (beginningIndex == -1 || endIndex == -1) {
                throw new IllegalArgumentException(
                        "Could not find the beginning or end sequence.");
            }
            return content.substring(
                    beginningIndex + INITIAL_MAIN_DEF_START.length(), endIndex);
        }
}
