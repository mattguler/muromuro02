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
    @Bean(name = "designApiWithPagination")
    public EvaluationCode getEvaluationCode(
            @Value("classpath:evaluators/DesignApiWithPaginationEval.java") Resource evaluatorResource)
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
