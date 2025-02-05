package com.muromuro.muromuro02.service.resourcemgmt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Configures the JavaInitialSolution beans by reading the initial solution files from
 * the resources.
 */
@Configuration
public class JavaInitialSolutionConfig {

    @Bean(name = "debugListSoln")
    public JavaInitialSolution getInitialSolutionForDebugList(
            @Value("classpath:/initialsolutions/java/DebugListSoln.java") Resource initialSolutionResource)
            throws IOException {
        return buildInitialSolution(initialSolutionResource);
    }

    @Bean(name = "duplicateRpcsSoln")
    public JavaInitialSolution getSolnForDuplicateRpcs(
            @Value("classpath:/initialsolutions/java/DuplicateRpcsSoln.java")
                    Resource initialSolutionResource) throws IOException {
        return buildInitialSolution(initialSolutionResource);
    }

    @Bean(name = "incompatibleInterfacesSoln")
    public JavaInitialSolution getSolnForIncompatibleInterfaces(
            @Value("classpath:/initialsolutions/java/IncompatibleInterfacesSoln.java")
                    Resource initialSolutionResource) throws IOException {
        return buildInitialSolution(initialSolutionResource);
    }

    @Bean(name = "longRunningFunctionsSoln")
    public JavaInitialSolution getSolnForLongRunningFunctions(
            @Value("classpath:initialsolutions/java/LongRunningFunctionsSoln.java") Resource initialSolutionResource)
            throws IOException {
        return buildInitialSolution(initialSolutionResource);
    }

    @Bean(name = "refactorTooManyIfsSoln")
    public JavaInitialSolution getInitialSolutionForRefactorTooManyIfs(
            @Value("classpath:initialsolutions/java/RefactorTooManyIfsSoln.java") Resource initialSolutionResource)
            throws IOException {
        return buildInitialSolution(initialSolutionResource);
    }

    private JavaInitialSolution buildInitialSolution(
            Resource initialSolutionResource) throws IOException {
        File initialSolutionFile = initialSolutionResource.getFile();
        String fileContent = new String(Files.readAllBytes(initialSolutionFile.toPath()));
        return new JavaInitialSolution(fileContent);
    }
}
