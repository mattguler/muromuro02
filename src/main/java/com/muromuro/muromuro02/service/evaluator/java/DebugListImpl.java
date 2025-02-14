package com.muromuro.muromuro02.service.evaluator.java;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import com.muromuro.muromuro02.model.UserInput;
import com.muromuro.muromuro02.service.docker.DockerProxy;
import com.muromuro.muromuro02.service.resourcemgmt.JavaEvaluationCode;
import com.muromuro.muromuro02.service.resourcemgmt.JavaInitialSolution;
import com.muromuro.muromuro02.service.utils.CodeUtils;
import com.muromuro.muromuro02.service.utils.Security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.regex.Pattern;

import static com.muromuro.muromuro02.service.utils.ValidationUtils.*;

@Service
public class DebugListImpl extends AbstractJavaEvaluatorImpl {

    private static final int USER_CODE_MAX_LENGTH = 2000;
    private static final int NUMBER_OF_TESTS = 2;

    private static final String IMMUTABLE_CODE_FRAGMENT =
            """
                            // Do not make any changes below these lines.
                    
                            public List<Integer> getSeries() {
                                return series;
                            }
                    
                            public void printSeries(String title) {
                                System.out.println(title);
                                for (int num : series) {
                                    System.out.println(num);
                                }
                            }
                    
                            public static void main(String[] args) {
                                SeriesProcessor sp1 = new SeriesProcessor();
                                sp1.printSeries("Iteration 1:");
                                SeriesProcessor sp2 = new SeriesProcessor();
                                sp2.printSeries("Iteration 2:");
                            }
                    """;

    private final Map<Integer, Pattern> passingTestPatterns =
            createTestMatcherPatterns(
                    "Test for iteration %d passed",
                    /* startTestId= */ 1,
                    /* endTestId= */ NUMBER_OF_TESTS);

    private final Map<Integer, Pattern> failingTestPatterns =
            createTestMatcherPatterns(
                    "Test for iteration %d failed",
                    /* startTestId= */ 1,
                    /* endTestId= */ NUMBER_OF_TESTS);

    private final JavaInitialSolution initialSolution;

    @Autowired
    public DebugListImpl(
            @Qualifier("javaDockerProxyImpl") DockerProxy dockerProxy,
            @Qualifier("debugList") JavaEvaluationCode evaluationCode,
            @Qualifier("debugListSoln") JavaInitialSolution initialSolution,
            @Qualifier("javaCodeUtilsImpl") CodeUtils codeUtils,
            @Qualifier("javaSecurityImpl") Security security) {
        super(dockerProxy, evaluationCode, codeUtils, security);
        this.initialSolution = initialSolution;
    }

    @Override
    public UserInput getInitialSolution() {
        return new UserInput("", initialSolution.getMainDefinition());
    }

    @Override
    protected int getUserCodeMaxLength() {
        return USER_CODE_MAX_LENGTH;
    }

    @Override
    protected JavaEvaluationCode buildEvaluationCode(UserInput userInput) {
        String mainDefinition = userInput.getMainDefinition();
        if (!mainDefinition.contains(IMMUTABLE_CODE_FRAGMENT)) {
            return evaluationCode
                    .updateBuildResponse(
                            new MuroMuroResponse(
                                    MuroMuroResponse.Status.FAILURE,
                                    "Changes not allowed in certain sections of the code."));
        }
        return evaluationCode
                .replaceMainDefinition(mainDefinition);
    }

    @Override
    protected MuroMuroResponse analyzeEvaluation(String dockerEvalOutput, UserInput userInput) {
        MuroMuroResponse initialAnalysis = analyzeForBasicErrors(dockerEvalOutput);
        if (initialAnalysis.getStatus() != MuroMuroResponse.Status.SUCCESS) {
            return initialAnalysis;
        }
        if (
                matchesTestPattern(
                        dockerEvalOutput, passingTestPatterns, /* testId= */ 1)
                && matchesTestPattern(
                        dockerEvalOutput, failingTestPatterns, /* testId= */ 2)) {
            // TODO: Maybe define a different error status code and style for
            // partially wrong answers.
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.FAILURE,
                    buildFailureMessage(
                            dockerEvalOutput,
                            "Getting closer. The first iteration is correct,\n"
                                    + "however the second iteration is still wrong."));
        }
        else if (matchesAnyTestPattern(dockerEvalOutput, failingTestPatterns)) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.FAILURE,
                    buildFailureMessage(
                            dockerEvalOutput,
                            "Incorrect solution. Fails validation."));
        }
        else if (matchesAllTestPatterns(dockerEvalOutput, passingTestPatterns)) {
            // TODO: Maybe also return the eval output here as well.
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.SUCCESS, buildSuccessMessage());
        }
        // We should not be reaching here.
        return new MuroMuroResponse(
                MuroMuroResponse.Status.UNKNOWN,
                buildFailureMessage(
                        dockerEvalOutput,
                        "Internal server failure.\n"
                                + "Please contact support with the following output."));
    }
}
