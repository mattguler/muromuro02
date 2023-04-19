package com.muromuro.muromuro02.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DockerClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DockerConfig {

    @Bean
    public DockerClient provideDockerClient() {
        return DockerClientBuilder.getInstance().build();
    }
}
