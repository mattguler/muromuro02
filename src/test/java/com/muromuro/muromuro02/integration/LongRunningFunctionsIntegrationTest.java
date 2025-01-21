package com.muromuro.muromuro02.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WithMockUser("interviewee")
@AutoConfigureMockMvc
public class LongRunningFunctionsIntegrationTest {

    private static final String GET_URL = "/muromuro_questions/long_running_functions";
    private static final String EVAL_URL = "/muromuro_questions/eval_long_running_functions";

    private static final String INITIAL_SOLUTION =
            """
                    public boolean runAllBlackBoxes(
                            List<BlackBox> blackBoxes,
                            Account account) {
                        for (int code = 0; code < 10; code++) {
                            for (BlackBox blackBox : blackBoxes) {
                                boolean result =
                                    blackBox.process(
                                            account,
                                            code,
                                            (acct, amount) ->
                                                    acct.setValue(acct.getValue() + amount));
                                if (!result) {
                                    return false;
                                }
                            }
                        }
                        return true;
                    }
                    """;

    private static final String WRONG_SOLUTION =
            """
                    public boolean runAllBlackBoxes(
                            List<BlackBox> blackBoxes,
                            Account account) {
                        for (int code = 0; code < 10; code++) {
                            final int finalCode = code;
                            List<Thread> threads = new ArrayList<>();
                            for (BlackBox blackBox : blackBoxes) {
                                Thread thread = new Thread(
                                    () ->
                                        blackBox.process(
                                            account,
                                            finalCode,
                                            (acct, amount) -> {
                                                synchronized(acct) {
                                                    acct.setValue(acct.getValue() + amount);
                                                }
                                            })
                                );
                                thread.start();
                                threads.add(thread);
                            }
                            try {
                                for (Thread thread : threads) {
                                    thread.join();
                                }
                            }
                            catch (InterruptedException e) {
                                System.err.println(e);
                                return false;
                            }
                        }
                        return true;
                    }
                    """;

    private static final String CORRECT_SOLUTION =
            """
                    // Returns true if no BlackBoxes fail during their process.
                    public boolean runAllBlackBoxes(
                            List<BlackBox> blackBoxes,
                            Account account) {
                        if (blackBoxes.isEmpty()) {
                            return true;
                        }
                        List<Callable<Boolean>> callables = new ArrayList<>();
                        for (int code = 0; code < 10; code++) {
                            final int finalCode = code;
                            for (BlackBox blackBox : blackBoxes) {
                                callables.add(
                                    () ->
                                        blackBox.process(
                                            account,
                                            finalCode,
                                            (acct, amount) -> {
                                                synchronized(acct) {
                                                    acct.setValue(acct.getValue() + amount);
                                                }
                                            }
                                        )
                                );
                            }
                        }
                        // Thread pools don't like being initialized with 0.
                        ExecutorService executor =
                            Executors.newFixedThreadPool(10 * blackBoxes.size() + 2);
                        try {
                            List<Future<Boolean>> results = executor.invokeAll(callables);
                            for (Future<Boolean> result : results) {
                                if (!result.get()) {
                                    return false;
                                }
                            }
                        }
                        catch (InterruptedException | ExecutionException e) {
                            System.err.println(e);
                            return false;
                        }
                        finally {
                            executor.shutdown();
                        }
                        return true;
                    }
                    """;

    @Autowired
    WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc =
                MockMvcBuilders
                        .webAppContextSetup(context)
                        .apply(springSecurity())
                        .build();
    }

    @Test
    public void testGetLongRunningFunctions() throws Exception {
        performGet()
                .validateContains("Long Running Functions")
                .validateContains(
                        "You are given a bunch of Black Box Processor objects");
    }

    @Test
    public void testEval_withInsecureInput() throws Exception {
        performEval("Runtime.getRuntime().exec(\\\"rm -rf /\\\");")
                .validateContains(
                        "Wrong answer: The solution seems insecure, "
                                + "with forbidden keyword: Runtime");
    }

    @Test
    public void testEval_withLengthyInput() throws Exception {
        performEval("a".repeat(5001))
                .validateContains(
                        "Wrong answer: The solution seems insecure, with its "
                                + "length exceeding the max allowable length.");
    }

    @Test
    public void testEval_withBadInput() throws Exception {
        performEval("blahblah")
                .validateContains("Wrong answer: Invalid solution.");
    }

    @Test
    public void testEval_withTimeout() throws Exception {
        performEval(INITIAL_SOLUTION)
                .validateContains(
                        "Server timed out: The solution took too long to execute.");
    }

    @Test
    public void testEval_withWrongAnswer() throws Exception {
        performEval(WRONG_SOLUTION)
                .validateContains(
                        "Wrong answer: Incorrect solution. Fails validation");
    }

    @Test
    public void testEval_withCorrectAnswer() throws Exception {
        performEval(CORRECT_SOLUTION)
                .validateContains(
                        "Correct answer: The solution looks correct, "
                                + "but your interviewer will be the final judge.");
    }

    private ResultWrapper performGet() throws Exception {
        ResultActions resultActions =
                this.mockMvc
                        .perform(get(GET_URL))
                        .andExpect(status().isOk());
        return new ResultWrapper(resultActions);
    }

    private ResultWrapper performEval(String mainDefinition) throws Exception {
        ResultActions resultActions =
                this.mockMvc
                        .perform(
                                post(EVAL_URL)
                                        .param("main-def-input", mainDefinition)
                                        .with(csrf()))
                        .andExpect(status().isOk());
        return new ResultWrapper(resultActions);
    }

    /** This is here to decrease code clutter in the integration tests. */
    private record ResultWrapper(ResultActions resultActions) {

        ResultWrapper validateContains(String str) throws Exception {
            this.resultActions().andExpect(content().string(containsString(str)));
            return this;
        }
    }
}
