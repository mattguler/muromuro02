package com.muromuro.muromuro02.service.evaluator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/** Configures the InitialSolution beans by reading the initial solution files from the resources. */
@Configuration
public class InitialSolutionConfig {

    @Bean(name = "debugListSoln")
    public InitialSolution getInitialSolutionForDebugList(
            @Value("classpath:/initialsolutions/DebugListSoln.java") Resource initialSolutionResource)
            throws IOException {
        return buildInitialSolution(initialSolutionResource);
    }

    @Bean(name = "refactorTooManyIfsSoln")
    public InitialSolution getInitialSolutionForRefactorTooManyIfs(
            @Value("classpath:initialsolutions/RefactorTooManyIfsSoln.java") Resource initialSolutionResource)
            throws IOException {
        return buildInitialSolution(initialSolutionResource);
    }

    private InitialSolution buildInitialSolution(
            Resource initialSolutionResource) throws IOException {
        File initialSolutionFile = initialSolutionResource.getFile();
        String fileContent = new String(Files.readAllBytes(initialSolutionFile.toPath()));
        return new InitialSolution(fileContent);
    }
}
