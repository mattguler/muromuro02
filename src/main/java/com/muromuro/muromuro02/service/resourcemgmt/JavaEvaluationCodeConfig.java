package com.muromuro.muromuro02.service.resourcemgmt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Configures the JavaEvaluationCode beans by reading the evaluator files from the resources.
 */
@Configuration
public class JavaEvaluationCodeConfig {

    @Bean(name = "debugList")
    public JavaEvaluationCode getEvaluationCodeForDebugList(
            @Value("classpath:evaluators/java/DebugListEval.java") Resource evaluatorResource)
            throws IOException {
        return buildEvaluationCode(evaluatorResource);
    }

    @Bean(name = "designApiWithPagination")
    public JavaEvaluationCode getEvaluationCodeForDesignApiWithPagination(
            @Value("classpath:evaluators/java/DesignApiWithPaginationEval.java") Resource evaluatorResource)
            throws IOException {
        return buildEvaluationCode(evaluatorResource);
    }

    @Bean(name = "detectSubstrings")
    public JavaEvaluationCode getEvaluationCodeForDetectSubstrings(
            @Value("classpath:evaluators/java/DetectSubstringsEval.java") Resource evaluatorResource)
            throws IOException {
        return buildEvaluationCode(evaluatorResource);
    }

    @Bean(name = "deviceDatabase")
    public JavaEvaluationCode getEvaluationCodeForDeviceDatabase(
            @Value("classpath:evaluators/java/DeviceDatabaseEval.java") Resource evaluatorResource)
            throws IOException {
        return buildEvaluationCode(evaluatorResource);
    }

    @Bean(name = "duplicateRpcs")
    public JavaEvaluationCode getEvalCodeForDuplicateRpcs(
            @Value("classpath:evaluators/java/DuplicateRpcsEval.java") Resource evaluatorResource)
            throws IOException {
        return buildEvaluationCode(evaluatorResource);
    }

    @Bean(name = "incompatibleInterfaces")
    public JavaEvaluationCode getEvalCodeForIncompatibleInterfaces(
            @Value("classpath:evaluators/java/IncompatibleInterfacesEval.java")
                    Resource evaluatorResource) throws IOException {
        return buildEvaluationCode(evaluatorResource);
    }

    @Bean(name = "longRunningFunctions")
    public JavaEvaluationCode getEvalCodeForLongRunningFunctions(
            @Value("classpath:evaluators/java/LongRunningFunctionsEval.java") Resource evaluatorResource)
            throws IOException {
        return buildEvaluationCode(evaluatorResource);
    }

    @Bean(name = "parseCsv")
    public JavaEvaluationCode getEvaluationCodeForParseCsv(
            @Value("classpath:evaluators/java/ParseCsvEval.java") Resource evaluatorResource)
            throws IOException {
        return buildEvaluationCode(evaluatorResource);
    }

    @Bean(name = "refactorTooManyIfs")
    public JavaEvaluationCode getEvaluationCodeForRefactorTooManyIfs(
            @Value("classpath:evaluators/java/RefactorTooManyIfsEval.java") Resource evaluatorResource)
            throws IOException {
        return buildEvaluationCode(evaluatorResource);
    }

    @Bean(name = "representAccountStates")
    public JavaEvaluationCode getEvaluationCodeForRepresentAccountStates(
            @Value("classpath:evaluators/java/RepresentAccountStatesEval.java") Resource evaluatorResource)
            throws IOException {
        return buildEvaluationCode(evaluatorResource);
    }

    private JavaEvaluationCode buildEvaluationCode(
            Resource evaluatorResource) throws IOException {
        File evaluatorFile = evaluatorResource.getFile();
        String evalFileContent = new String(Files.readAllBytes(evaluatorFile.toPath()));
        return new JavaEvaluationCode(evalFileContent);
    }
}
