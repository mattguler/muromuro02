package com.muromuro.muromuro02.service;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import com.muromuro.muromuro02.service.evaluator.Evaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

// TODO: Remove this class and use the Evaluator directly from the Controller.
@Service
public class MuroMuroServiceImpl implements MuroMuroService {

    private final Evaluator representAccountStates;

    @Autowired
    public MuroMuroServiceImpl(
            @Qualifier("representAccountStatesImpl") Evaluator representAccountStates) {
        this.representAccountStates = representAccountStates;
    }

    @Override
    public String getRepresentAccountStatesInitSolution() {
        return representAccountStates.getInitialSolution();
    }

    @Override
    public MuroMuroResponse evalRepresentAccountStatesSolution(String userInput) {
        return representAccountStates.evaluateSolution(userInput);
    }
}
