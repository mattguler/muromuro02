package com.muromuro.muromuro02.service.docker;

/**
 * The interface for the Docker client proxy.
 * Used to connect to a Docker container in order to evaluate
 * the solution of a MuroMuro question.
 */
public interface DockerProxy {
    /**
     * Starts a Docker container and evaluates the user input solution.
     * @return The ID of the started container.
     */
    String startContainer(String evaluatorCode);

    /**
     * Returns the evaluation output from the Docker container with the given ID.
     */
    String getContainerOutput(String containerId);

    /**
     * Cleans up and removes the Docker container with the given ID.
     */
    void cleanUpContainer(String containerId);
}
