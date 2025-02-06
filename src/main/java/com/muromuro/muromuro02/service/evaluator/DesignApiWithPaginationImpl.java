package com.muromuro.muromuro02.service.evaluator;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import com.muromuro.muromuro02.model.UserInput;
import com.muromuro.muromuro02.service.docker.DockerProxy;
import com.muromuro.muromuro02.service.resourcemgmt.JavaEvaluationCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.regex.Pattern;

import static com.muromuro.muromuro02.service.utils.Utils.*;

/** The evaluator for the DesignApiWithPagination question. */
@Service
public class DesignApiWithPaginationImpl extends AbstractEvaluatorImpl {

    private static final int USER_CODE_MAX_LENGTH = 2500;
    private static final int NUMBER_OF_TESTS = 4;

    private static final String INITIAL_CALLER_CODE =
            """
                    // Here is the code for the caller function that is supposed to call your API.
                    // All the employee lists to be populated by your API are included here for the
                    // sake of this question. Please implement the calls to your API function here,
                    // and populate all 4 lists in order, with the Employee objects returned from
                    // the calls to your API function. Each list is going to take in a maxSize
                    // number of employees:
                    public void callerFunction(
                        List<Employee> list1,  // Populate and return to the caller.
                        List<Employee> list2,  // Populate and return to the caller.
                        List<Employee> list3,  // Populate and return to the caller.
                        List<Employee> list4,  // Populate and return to the caller.
                        int maxSize,  // The maximum number of employees to be placed in each list.
                        long companyId,  // The company ID to be used in the database calls. Pass it into your API.
                        DatabaseProxy dbProxy  // Please pass this into your API function as well.
                    ) {
                        // Make call to your function here.
                    }
                    """;

    private static final String INITIAL_MAIN_DEFINITION =
            """
                    // Please write your API function implementation here.
                    // Please make sure to include the pagination capability.
                    // Your API function input parameters should contain the companyID and the
                    // dbProxy at the very least, along with the necessary input parameters for
                    // pagination. Your function should return List<Employee> as a subset of the
                    // employees, in the same sorted order as they are returned from the database.
                    """;

    private final Map<Integer, Pattern> passingTestPatterns =
            createTestMatcherPatterns(
                    "List %d is correctly formed",
                    /* startTestId= */ 1,
                    /* endTestId= */ NUMBER_OF_TESTS);

    private final Map<Integer, Pattern> failingTestPatterns =
            createTestMatcherPatterns(
                    "List %d is incorrectly formed",
                    /* startTestId= */ 1,
                    /* endTestId= */ NUMBER_OF_TESTS);

    @Autowired
    public DesignApiWithPaginationImpl(
            DockerProxy dockerProxy,
            @Qualifier("designApiWithPagination") JavaEvaluationCode evaluationCode) {
        super(dockerProxy, evaluationCode);
    }

    @Override
    public UserInput getInitialSolution() {
        return new UserInput(INITIAL_CALLER_CODE, INITIAL_MAIN_DEFINITION);
    }

    @Override
    protected int getUserCodeMaxLength() {
        return USER_CODE_MAX_LENGTH;
    }

    @Override
    protected JavaEvaluationCode buildEvaluationCode(UserInput userInput) {
        return evaluationCode
                .replaceCallerCode(userInput.getCallerCode())
                .replaceMainDefinition(userInput.getMainDefinition());
    }

    @Override
    protected MuroMuroResponse analyzeEvaluation(
            String dockerEvalOutput, UserInput unusedUserInput) {
        MuroMuroResponse initialAnalysis = analyzeForBasicErrors(dockerEvalOutput);
        if (initialAnalysis.getStatus() != MuroMuroResponse.Status.SUCCESS) {
            return initialAnalysis;
        }
        if (matchesAnyTestPattern(dockerEvalOutput, failingTestPatterns)) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.FAILURE,
                    buildFailureMessage(
                            dockerEvalOutput,
                            "Incorrect solution. One or more of the lists are not\n"
                                    + "correctly populated."));
        }
        else if (matchesAllTestPatterns(dockerEvalOutput, passingTestPatterns)) {
            return new MuroMuroResponse(
                    MuroMuroResponse.Status.SUCCESS, buildSuccessMessage());
        }

        return new MuroMuroResponse(
                MuroMuroResponse.Status.UNKNOWN,
                "Internal server failure. Please contact support with the following output:\n"
                        + dockerEvalOutput);
    }
}
