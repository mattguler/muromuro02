package com.muromuro.muromuro02.service.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.LogContainerCmd;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DockerProxyImpl implements DockerProxy {

    private final DockerClient dockerClient;

    @Autowired
    public DockerProxyImpl(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    @Override
    public String startContainer(String evaluatorCode) {
        String imageId = "openjdk:11";
        String containerName = UUID.randomUUID().toString();
        String[] command = {
                "sh",
                "-c",
                "echo \""
                        + evaluatorCode
                        + "\" > Evaluator.java; javac Evaluator.java; java Evaluator"};
        CreateContainerResponse container =
                dockerClient.createContainerCmd(imageId)
                        .withCmd(command)
                        .withName(containerName)
                        .exec();
        dockerClient.startContainerCmd(container.getId()).exec();
        return container.getId();
    }

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

    @Override
    public void cleanUpContainer(String containerId) {
        dockerClient.removeContainerCmd(containerId).exec();
    }
}
