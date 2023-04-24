package com.muromuro.muromuro02.service.docker;

public interface DockerProxy {
    String startContainer(String evaluatorCode);
    String getContainerOutput(String containerId);
    void cleanUpContainer(String containerId);
}
