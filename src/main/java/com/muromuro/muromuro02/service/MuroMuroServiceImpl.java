package com.muromuro.muromuro02.service;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import com.muromuro.muromuro02.service.docker.DockerProxy;
import com.muromuro.muromuro02.service.evaluator.Evaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class MuroMuroServiceImpl implements MuroMuroService {

    private final DockerProxy dockerProxy;
    private final Evaluator representAccountStates;

    @Autowired
    public MuroMuroServiceImpl(
            DockerProxy dockerProxy,
            @Qualifier("representAccountStatesImpl") Evaluator representAccountStates) {
        this.dockerProxy = dockerProxy;
        this.representAccountStates = representAccountStates;
    }

    @Override
    public String getRepresentAccountStatesInitSolution() {
        return representAccountStates.getInitialSolution();
    }

    @Override
    public MuroMuroResponse evalRepresentAccountStatesSolution(String userInput) {
        MuroMuroResponse securityResponse =
                representAccountStates.checkIfCodeSecure(userInput);
        if (securityResponse.getStatus() != MuroMuroResponse.Status.SUCCESS) {
            return securityResponse;
        }
        String combinedCode =
                representAccountStates.buildEvaluationCode(userInput);
        String containerId = dockerProxy.startContainer(combinedCode);
        String output = dockerProxy.getContainerOutput(containerId);
        // Note: Comment this out when debugging the container logs.
        dockerProxy.cleanUpContainer(containerId);
        return representAccountStates.analyzeEvaluation(output);
    }
}
