package com.muromuro.muromuro02.service.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DockerClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The configuration for the Docker client.
 * In order to run MuroMuro on your server, you first need to have a Docker daemon
 * installed and running. Follow the instructions on the Docker website to install Docker.
 * Once Docker is installed, run the following command to install the openjdk:11 Docker image
 * on your server, for the Docker daemon to use:
 * $ docker pull openjdk:11
 */
@Configuration
public class DockerConfig {

    @Bean
    public DockerClient provideDockerClient() {
        return DockerClientBuilder.getInstance().build();
    }
}
