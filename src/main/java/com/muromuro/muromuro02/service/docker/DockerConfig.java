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
 *
 * If the Docker Desktop is not opening properly (which sometimes seems to happen),
 * run the following commands on your terminal to completely remove it.
 * (These commands apply to MAC OS. They might be different for Linux machines.)
 *
 * #!/bin/bash
 * sudo rm -rf /Applications/Docker.app
 * sudo rm -rf ~/Library/Group\ Containers/group.com.docker
 * sudo rm -rf ~/Library/Containers/com.docker.docker
 * sudo rm -rf ~/Library/Application\ Support/Docker\ Desktop
 * sudo rm -rf ~/.docker
 * sudo rm /usr/local/bin/docker
 * sudo rm /usr/local/bin/docker-compose
 * sudo rm /usr/local/bin/com.docker.cli
 * sudo rm /usr/local/bin/docker-credential-desktop
 * sudo rm /usr/local/bin/docker-credential-ecr-login
 * sudo rm /usr/local/bin/docker-credential-osxkeychain
 * sudo rm /usr/local/bin/hub-tool
 * sudo rm /usr/local/bin/hyperkit
 * sudo rm /usr/local/bin/kubectl.docker
 * sudo rm /usr/local/bin/notary
 * sudo rm /usr/local/bin/vpnkit
 * sudo rm -rf ~/Library/Logs/Docker\ Desktop
 * sudo rm -rf /usr/local/lib/docker
 * sudo rm -rf /var/tmp/docker.*
 * # Remove any Docker networks that may exist (optional but recommended for a clean start):
 * docker network prune -f
 *
 * Restart your machine once these commands have run successfully.
 * Afterwards, download a new Docker image and go through the installation process.
 *
 * Once Docker is installed, go to Settings -> Resources, and decrease all the resource
 * allocation to amounts manageable on your VPS or Cloud Server.
 * You may also want to disable the Resource Saver, since it has caused problems before
 * with the Docker service not being responsive to the Muromuro server requests.
 *
 * When all the settings are done, run the following command to install the openjdk image
 * on your server, for the Docker daemon to use:
 * $ sudo docker pull openjdk:17
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
