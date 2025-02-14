package com.muromuro.muromuro02.service.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.LogContainerCmd;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import com.muromuro.muromuro02.service.utils.CodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * The implementation of the Docker proxy for evaluating Java solutions.
 * TODO: Write some tests for this class.
 */
@Service
public class JavaDockerProxyImpl implements DockerProxy {

    private static final int TIMEOUT_IN_SECONDS = 10;

    private final DockerClient dockerClient;
    private final CodeUtils codeUtils;

    @Autowired
    public JavaDockerProxyImpl(
            DockerClient dockerClient,
            @Qualifier("javaCodeUtilsImpl") CodeUtils codeUtils) {
        this.dockerClient = dockerClient;
        this.codeUtils = codeUtils;
    }

    /**
     * Starts a Docker container and evaluates the user input solution.
     * @return The ID of the started container.
     */
    @Override
    public String startContainer(String evaluatorCode) {
        String imageId = "openjdk:17";
        String containerName = UUID.randomUUID().toString();
        String className = codeUtils.getClassName(evaluatorCode);
        if (className == null) {
            throw new IllegalArgumentException(
                    "Could not find class name in given code:\n" + evaluatorCode);
        }
        String[] command = {
                "sh",
                "-c",
                String.format(
                        "echo \"%s\" > %s.java; "
                                + "javac %s.java; "
                                + "timeout -s SIGKILL %d java %s",
                        evaluatorCode,
                        className,
                        className,
                        TIMEOUT_IN_SECONDS,
                        className)};

        CreateContainerResponse container =
                dockerClient.createContainerCmd(imageId)
                        .withCmd(command)
                        .withName(containerName)
                        .exec();
        dockerClient.startContainerCmd(container.getId()).exec();
        return container.getId();
    }

    /**
     * Returns the evaluation output from the Docker container with the given ID.
     */
    @Override
    public String getContainerOutput(String containerId) {
        LogContainerCmd cmd =
                dockerClient.logContainerCmd(containerId)
                        .withStdOut(true)
                        .withStdErr(true)
                        .withFollowStream(true)
                        .withTailAll();
        final StringBuilder logOutput = new StringBuilder();
        LogContainerResultCallback callback =
                new LogContainerResultCallback() {
                    @Override
                    public void onNext(Frame item) {
                        logOutput.append(new String(item.getPayload()));
                    }
                };
        try {
            cmd.exec(callback).awaitCompletion();
        }
        catch (InterruptedException e) {
            logOutput.append("ERROR: Container execution was interrupted:\n" + e);
        }
        return logOutput.toString();
    }

    /**
     * Cleans up and removes the Docker container with the given ID.
     */
    @Override
    public void cleanUpContainer(String containerId) {
        dockerClient.removeContainerCmd(containerId).exec();
    }
}
