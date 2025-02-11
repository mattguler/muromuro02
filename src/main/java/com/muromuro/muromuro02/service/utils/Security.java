package com.muromuro.muromuro02.service.utils;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import com.muromuro.muromuro02.model.UserInput;

/**
 * The Security interface which is used to validate the security of the user's solution.
 */
public interface Security {

    enum Options {
        ENABLE_MULTI_THREAD_SUPPORT,
        ENABLE_FILE_IO_SUPPORT
    }

    MuroMuroResponse validateCodeLength(UserInput userInput, int maxLength);

    MuroMuroResponse checkIfCodeSecure(UserInput userInput, Security.Options... options);
}
