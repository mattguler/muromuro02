package com.muromuro.muromuro02.service;

import com.muromuro.muromuro02.model.MuroMuroResponse;

public interface MuroMuroService {

    String getRepresentAccountStatesInitSolution();

    MuroMuroResponse evalRepresentAccountStatesSolution(String userInput);
}
