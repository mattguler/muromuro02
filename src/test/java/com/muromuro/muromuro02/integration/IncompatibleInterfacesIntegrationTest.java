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
public class IncompatibleInterfacesIntegrationTest {
    private static final String GET_URL = "/muromuro_questions/incompatible_interfaces";
    private static final String EVAL_URL = "/muromuro_questions/eval_incompatible_interfaces";

    private static final String MAIN_DEFINITION_TEMPLATE =
            """
                    // The field definitions.
                    %s
                    
                    // Constructor.
                    public MyClass(
                            Client client,
                            Processor processor1,
                            Processor2 processor2,
                            Processor3 processor3
                            ) {
                        // Constructor definition.
                        %s
                    }
                    
                    // Method definition.
                    %s
                    """;

    private static final String DEFAULT_FIELDS =
            """
                    private final Client client;
                    private final Processor processor1;
                    private final Processor2 processor2;
                    private final Processor3 processor3;
                    """;

    private static final String MAP_FIELDS =
            """
                    private final Client client;
                    private final Processor processor1;
                    private final Map<Integer, Processor> processorMap;
                    """;

    private static final String DEFAULT_CONSTRUCTOR =
            """
                    this.client = client;
                    this.processor1 = processor1;
                    this.processor2 = processor2;
                    this.processor3 = processor3;
                    """;

    private static final String TIMEOUT_CONSTRUCTOR =
            """
                    this.client = client;
                    this.processor1 = processor1;
                    this.processor2 = processor2;
                    this.processor3 = processor3;
                    while(true) { }
                    """;

    private static final String MAP_CONSTRUCTOR =
            """
                    this.client = client;
                    this.processor1 = processor1;
                    Processor proc2Adapter =
                        (x, y) -> processor2.makeCalculations(x, y - 1);
                    Processor proc3Adapter =
                        (x, y) -> processor3.execute(x, y + 1);
                    this.processorMap =
                        Map.ofEntries(
                            Map.entry(1, proc3Adapter),
                            Map.entry(2, proc2Adapter),
                            Map.entry(3, proc2Adapter),
                            Map.entry(4, proc3Adapter));
                    """;

    private static final String DEFAULT_METHOD =
            """
                    public int runProcess(int x) {
                        if (x == 2 || x == 3) {
                            return client.runProcess(processor2, x);
                        }
                        else if (x == 1 || x == 4) {
                            return client.runProcess(processor3, x);
                        }
                        else {
                            return client.runProcess(processor1, x);
                        }
                    }
                    """;

    private static final String WRONG_METHOD =
            """
                    public int runProcess(int x) {
                        return 0;
                    }
                    """;

    private static final String PARTIALLY_WRONG_METHOD =
            """
                    public int runProcess(int x) {
                        if (x == 2 || x == 3) {
                            return client.runProcess(
                                (xx, yy) -> processor2.makeCalculations(xx, yy),
                                x);
                        }
                        else if (x == 1 || x == 4) {
                            return client.runProcess(
                                (xx, yy) -> processor3.execute(xx, yy),
                                x);
                        }
                        else {
                            return client.runProcess(processor1, x);
                        }
                    }
                    """;

    private static final String PARTIALLY_CORRECT_METHOD =
            """
                    public int runProcess(int x) {
                        if (x == 2 || x == 3) {
                            return client.runProcess(
                                (xx, yy) -> processor2.makeCalculations(xx, yy - 1),
                                x);
                        }
                        else if (x == 1 || x == 4) {
                            return client.runProcess(
                                (xx, yy) -> processor3.execute(xx, yy + 1),
                                x);
                        }
                        else {
                            return client.runProcess(processor1, x);
                        }
                    }
                    """;

    private static final String MAP_METHOD =
            """
                    public int runProcess(int x) {
                        Processor processor =
                            processorMap.getOrDefault(x, processor1);
                        return client.runProcess(processor, x);
                    }
                    """;

    private static String buildSolution(
            String fields, String constructor, String method) {
        return String.format(
                MAIN_DEFINITION_TEMPLATE, fields, constructor, method);
    }

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
    public void testGetIncompatibleInterfaces() throws Exception {
        performGet()
                .validateContains("Incompatible Interfaces")
                .validateContains(
                        "You now have to use more processors to get the job done.");
    }

    @Test
    public void testEval_withInsecureInput() throws Exception {
        performEval("Process prc;")
                .validateContains(
                        "Wrong answer: The solution seems insecure, "
                                + "with forbidden keyword: Process");
    }

    @Test
    public void testEval_withImport() throws Exception {
        performEval("import java.util.*;")
                .validateContains(
                        "Wrong answer: The code should not contain "
                                + "any import statements.");
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
        String mainDefinition =
                buildSolution(DEFAULT_FIELDS, TIMEOUT_CONSTRUCTOR, WRONG_METHOD);
        performEval(mainDefinition)
                .validateContains("Server timed out");
    }

    @Test
    public void testEval_withWrongAnswer() throws Exception {
        String mainDefinition =
                buildSolution(DEFAULT_FIELDS, DEFAULT_CONSTRUCTOR, DEFAULT_METHOD);
        performEval(mainDefinition)
                .validateContains("Wrong answer: Invalid solution.");
    }

    @Test
    public void testEval_withWrongAnswer_2() throws Exception {
        String mainDefinition =
                buildSolution(DEFAULT_FIELDS, DEFAULT_CONSTRUCTOR, WRONG_METHOD);
        performEval(mainDefinition)
                .validateContains(
                        "Wrong answer: Incorrect solution. Fails validation.");
    }

    @Test
    public void testEval_withPartiallyWrongAnswer() throws Exception {
        String mainDefinition =
                buildSolution(
                        DEFAULT_FIELDS,
                        DEFAULT_CONSTRUCTOR,
                        PARTIALLY_WRONG_METHOD);
        performEval(mainDefinition)
                .validateContains("Wrong answer: Getting closer.");
    }

    @Test
    public void testEval_withPartiallyCorrectAnswer() throws Exception {
        String mainDefinition =
                buildSolution(
                        DEFAULT_FIELDS,
                        DEFAULT_CONSTRUCTOR,
                        PARTIALLY_CORRECT_METHOD);
        performEval(mainDefinition)
                .validateContains(
                        "Correct answer: However, the solution could be better");
    }

    @Test
    public void testEval_withCorrectAnswer() throws Exception {
        String mainDefinition =
                buildSolution(
                        MAP_FIELDS,
                        MAP_CONSTRUCTOR,
                        MAP_METHOD);
        performEval(mainDefinition)
                .validateContains("Correct answer: The solution looks correct");
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
                                        .param("userInput.mainDefinition", mainDefinition)
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
