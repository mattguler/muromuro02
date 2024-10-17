package com.muromuro.muromuro02.service.evaluator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/** Configures the EvaluationCode beans by reading the evaluator files from the resources. */
@Configuration
public class EvaluationCodeConfig {

    @Bean(name = "debugList")
    public EvaluationCode getEvaluationCodeForDebugList(
            @Value("classpath:evaluators/DebugListEval.java") Resource evaluatorResource)
            throws IOException {
        return buildEvaluationCode(evaluatorResource);
    }

    @Bean(name = "designApiWithPagination")
    public EvaluationCode getEvaluationCodeForDesignApiWithPagination(
            @Value("classpath:evaluators/DesignApiWithPaginationEval.java") Resource evaluatorResource)
            throws IOException {
        return buildEvaluationCode(evaluatorResource);
    }

    @Bean(name = "detectSubstrings")
    public EvaluationCode getEvaluationCodeForDetectSubstrings(
            @Value("classpath:evaluators/DetectSubstringsEval.java") Resource evaluatorResource)
            throws IOException {
        return buildEvaluationCode(evaluatorResource);
    }

    @Bean(name = "deviceDatabase")
    public EvaluationCode getEvaluationCodeForDeviceDatabase(
            @Value("classpath:evaluators/DeviceDatabaseEval.java") Resource evaluatorResource)
            throws IOException {
        return buildEvaluationCode(evaluatorResource);
    }

    @Bean(name = "parseCsv")
    public EvaluationCode getEvaluationCodeForParseCsv(
            @Value("classpath:evaluators/ParseCsvEval.java") Resource evaluatorResource)
            throws IOException {
        return buildEvaluationCode(evaluatorResource);
    }

    @Bean(name = "refactorTooManyIfs")
    public EvaluationCode getEvaluationCodeForRefactorTooManyIfs(
            @Value("classpath:evaluators/RefactorTooManyIfsEval.java") Resource evaluatorResource)
            throws IOException {
        return buildEvaluationCode(evaluatorResource);
    }

    @Bean(name = "representAccountStates")
    public EvaluationCode getEvaluationCodeForRepresentAccountStates(
            @Value("classpath:evaluators/RepresentAccountStatesEval.java") Resource evaluatorResource)
            throws IOException {
        return buildEvaluationCode(evaluatorResource);
    }

    private EvaluationCode buildEvaluationCode(
            Resource evaluatorResource) throws IOException {
        File evaluatorFile = evaluatorResource.getFile();
        String evalFileContent = new String(Files.readAllBytes(evaluatorFile.toPath()));
        return new EvaluationCode(evalFileContent);
    }
}
