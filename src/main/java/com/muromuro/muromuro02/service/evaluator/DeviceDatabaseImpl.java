package com.muromuro.muromuro02.service.evaluator;

import com.muromuro.muromuro02.model.MuroMuroResponse;
import com.muromuro.muromuro02.model.UserInput;
import com.muromuro.muromuro02.service.docker.DockerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import static com.muromuro.muromuro02.service.utils.Utils.prependStaticIfMissing;

/** The evaluator for the DeviceDatabase question. */
@Service
public class DeviceDatabaseImpl extends AbstractEvaluatorImpl {
    private static final int USER_CODE_MAX_LENGTH = 2500;

    @Autowired
    public DeviceDatabaseImpl(
            DockerProxy dockerProxy,
            @Qualifier("deviceDatabase") EvaluationCode evaluationCode) {
        super(dockerProxy, evaluationCode);
    }

    @Override
    protected int getUserCodeMaxLength() {
        return USER_CODE_MAX_LENGTH;
    }

    @Override
    protected String buildEvaluationCode(UserInput userInput) {
        String callerCode = userInput.getCallerCode();
        String mainDefinition = userInput.getMainDefinition();
        // TODO: Get rid of this prepend-static feature.
        mainDefinition = prependStaticIfMissing(mainDefinition);
        return evaluationCode
                .replaceCallerCode(callerCode)
                .replaceMainDefinition(mainDefinition)
                .getFormattedContent();
    }

    @Override
    protected MuroMuroResponse analyzeEvaluation(
            String dockerEvalOutput, UserInput userInput) {

        // TODO: Implement the rest of the Device Database evaluator.

        return new MuroMuroResponse(
                MuroMuroResponse.Status.UNKNOWN,
                "The Device Database evaluator output so far:\n"
                        + dockerEvalOutput);
    }
}
