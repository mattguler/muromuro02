package com.muromuro.muromuro02.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.LogContainerCmd;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import com.muromuro.muromuro02.model.MuroMuroResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MuroMuroServiceImpl implements MuroMuroService {

    private static final String REPRESENT_ACCOUNT_STATES_INIT_SOLUTION =
            "boolean isActive = false;";

    private static final String REPRESENT_ACCOUNT_STATES_EVALUATOR_PREFIX =
            "public class Evaluator {\n\n";

    private static final String REPRESENT_ACCOUNT_STATES_EVALUATOR_SUFFIX =
            "\n\nstatic void evalAccountState(AccountState accountState) {\n"
                    + "  switch(accountState) {\n"
                    + "    case INACTIVE:\n"
                    + "      System.out.println(\\\"Account state INACTIVE.\\\");\n"
                    + "      break;\n"
                    + "    case ACTIVE:\n"
                    + "      System.out.println(\\\"Account state ACTIVE.\\\");\n"
                    + "      break;\n"
                    + "    case SUSPENDED:\n"
                    + "      System.out.println(\\\"Account state SUSPENDED.\\\");\n"
                    + "      break;\n"
                    + "    case DELETED:\n"
                    + "      System.out.println(\\\"Account state DELETED.\\\");\n"
                    + "      break;\n"
                    + "    default:\n"
                    + "      System.out.println(\\\"Invalid account state.\\\");\n"
                    + "      break;\n"
                    + "  }\n"
                    + "}\n\n"
                    + "public static void main(String[] args) {\n"
                    + "  evalAccountState(AccountState.INACTIVE);\n"
                    + "  evalAccountState(AccountState.ACTIVE);\n"
                    + "  evalAccountState(AccountState.SUSPENDED);\n"
                    + "  evalAccountState(AccountState.DELETED);\n"
                    + "}\n"
                    + "}\n"; // This closes out the class definition in the prefix.

    private final DockerClient dockerClient;

    @Autowired
    public MuroMuroServiceImpl(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    @Override
    public String getRepresentAccountStatesInitSolution() {
        return REPRESENT_ACCOUNT_STATES_INIT_SOLUTION;
    }

    @Override
    public MuroMuroResponse evalRepresentAccountStatesSolution(String userInput) {
        String combinedCode =
                REPRESENT_ACCOUNT_STATES_EVALUATOR_PREFIX
                        + userInput
                        + REPRESENT_ACCOUNT_STATES_EVALUATOR_SUFFIX;
        String containerId = startContainer(combinedCode);
        String output = getContainerOutput(containerId);
//        stopContainer(containerId);
        if (output.contains("Account state INACTIVE")
                && output.contains("Account state ACTIVE")
                && output.contains("Account state SUSPENDED")
                && output.contains("Account state DELETED")) {
            return new MuroMuroResponse(MuroMuroResponse.Status.SUCCESS);
        }
        else if (output.contains("error: cannot find symbol")) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.FAILURE,
                    "The solution does not seem to represent all the necessary account states.");
        }
        else {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.FAILURE,
                    "The solution failed with error:\n"
                            + output);
        }
    }

    private String startContainer(String evaluatorCode) {
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

    private String getContainerOutput(String containerId) {
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

    private void stopContainer(String containerId) {
        dockerClient.stopContainerCmd(containerId).exec();
        dockerClient.removeContainerCmd(containerId).exec();
    }
}
