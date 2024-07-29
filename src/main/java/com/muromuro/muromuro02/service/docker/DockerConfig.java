package com.muromuro.muromuro02.service.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.zerodep.ZerodepDockerHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The configuration for the Docker client.
 * In order to run MuroMuro on your server, you first need to have a Docker daemon
 * installed and running. Follow the instructions on the Docker website to install Docker.
 * Once Docker is installed, run the following command to install the openjdk:11 Docker image
 * on your server, for the Docker daemon to use:
 * $ docker pull openjdk:17
 */
@Configuration
public class DockerConfig {

    private static final String DOCKER_DEFAULT_SOCKET = "unix:///var/run/docker.sock";
    private static final int MAX_CONNECTIONS = 100;

    @Bean
    public DockerClientConfig provideDockerClientConfig() {
        return DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(DOCKER_DEFAULT_SOCKET)
                // TODO: Maybe enable TLS verification in the future, for a more secure connection.
                .withDockerTlsVerify(false)
                .build();
    }

    // NOTE: We need the ZerodepDockerHttpClient due to the issues that DockerClient has
    // with the Spring Boot version 3 Jakarta dependencies.
    @Bean
    public ZerodepDockerHttpClient provideZerodepDockerHttpClient(
            DockerClientConfig dockerClientConfig) {
        return new ZerodepDockerHttpClient.Builder()
                .dockerHost(dockerClientConfig.getDockerHost())
                .sslConfig(dockerClientConfig.getSSLConfig())
                .maxConnections(MAX_CONNECTIONS)
                .build();
    }

    @Bean
    public DockerClient provideDockerClient(
            DockerClientConfig dockerClientConfig, ZerodepDockerHttpClient dockerHttpClient) {
        return DockerClientImpl.getInstance(dockerClientConfig, dockerHttpClient);
    }
}
