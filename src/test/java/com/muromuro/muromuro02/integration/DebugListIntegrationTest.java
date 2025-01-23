package com.muromuro.muromuro02.integration;

import com.muromuro.muromuro02.model.UserInput;
import com.muromuro.muromuro02.service.evaluator.Evaluator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

/** Integration tests for the Debug List Muromuro question. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WithMockUser("interviewee")
@AutoConfigureMockMvc
public class DebugListIntegrationTest {

    private static final String GET_URL = "/muromuro_questions/debug_list";
    private static final String EVAL_URL = "/muromuro_questions/eval_debug_list";

    private static final String MAIN_DEFINITION_TEMPLATE =
            """
                        public static final class SeriesProcessor {
                            // The series declaration.
                            %s
                           
                            // The constructor definition.
                            %s
                           
                            // The createFiveElements() definition.
                            %s
                            
                            // The addFiveElements() definition.
                            %s
                            
                            // The removeFirstFiveElements() definition.
                            %s
                            
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
                        }
                    """;

    private static final String NON_STATIC_SERIES =
            "private List<Integer> series;";

    private static final String STATIC_SERIES =
            "private static List<Integer> series = createFiveElements();";

    private static final String CONSTRUCTOR_WITH_SERIES =
            """
                    public SeriesProcessor() {
                        series = createFiveElements();
                        addFiveElements(series);
                        removeFirstFiveElements(series);
                    }
                    """;

    private static final String CONSTRUCTOR_WITHOUT_SERIES =
            """
                    public SeriesProcessor() {
                        addFiveElements(series);
                        removeFirstFiveElements(series);
                    }
                    """;

    private static final String CONSTRUCTOR_WITH_TIMEOUT =
            """
                    public SeriesProcessor() {
                        series = createFiveElements();
                        addFiveElements(series);
                        removeFirstFiveElements(series);
                        while (true) { }
                    }
                    """;

    private static final String CREATE_NON_STATIC =
            """
                    private List<Integer> createFiveElements() {
                        List<Integer> series = new ArrayList<>();
                        for (int i = 0; i < 5; i++) {
                            series.add(i);
                        }
                        return series;
                    }
                    """;

    private static final String CREATE_WITH_STATIC =
            """
                    private static List<Integer> createFiveElements() {
                        List<Integer> series = new ArrayList<>();
                        for (int i = 0; i < 5; i++) {
                            series.add(i);
                        }
                        return series;
                    }
                    """;

    private static final String ADD_WITH_BUG =
            """
                    private void addFiveElements(List<Integer> series) {
                        int lastNum = series.get(series.size() - 1);
                        for (int i = 0; i < 5; i++) {
                            series.add(lastNum + i);
                        }
                    }
                    """;

    private static final String ADD_WITHOUT_BUG =
            """
                    private void addFiveElements(List<Integer> series) {
                        int lastNum = series.get(series.size() - 1);
                        for (int i = 0; i < 5; i++) {
                            series.add(lastNum + i + 1);
                        }
                    }
                    """;

    private static final String REMOVE_WITH_BUG =
            """
                    private void removeFirstFiveElements(List<Integer> series) {
                        for (int i = 0; i < 5; i++) {
                            series.remove(i);
                        }
                    }
                    """;

    private static final String REMOVE_WITHOUT_BUG =
            """
                    private void removeFirstFiveElements(List<Integer> series) {
                        for (int i = 0; i < 5; i++) {
                            series.remove(0);
                        }
                    }
                    """;

    private static class TestDataBuilder {
        private String seriesDef;
        private String constructorDef;
        private String createDef;
        private String addDef;
        private String removeDef;

        // By default, this builds the wrong initial solution.
        TestDataBuilder() {
            this.seriesDef = NON_STATIC_SERIES;
            this.constructorDef = CONSTRUCTOR_WITH_SERIES;
            this.createDef = CREATE_NON_STATIC;
            this.addDef = ADD_WITH_BUG;
            this.removeDef = REMOVE_WITH_BUG;
        }

        TestDataBuilder withSeriesDef(String seriesDef) {
            this.seriesDef = seriesDef;
            return this;
        }

        TestDataBuilder withConstructorDef(String constructorDef) {
            this.constructorDef = constructorDef;
            return this;
        }

        TestDataBuilder withCreateDef(String createDef) {
            this.createDef = createDef;
            return this;
        }

        TestDataBuilder withAddDef(String addDef) {
            this.addDef = addDef;
            return this;
        }

        TestDataBuilder withRemoveDef(String removeDef) {
            this.removeDef = removeDef;
            return this;
        }

        String build() {
            return String.format(
                    MAIN_DEFINITION_TEMPLATE,
                    seriesDef,
                    constructorDef,
                    createDef,
                    addDef,
                    removeDef);
        }
    }

    // For initial-code testing.
    @Autowired
    @Qualifier("debugListImpl")
    Evaluator debugListEval;

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
    public void testGetDebugList() throws Exception {
        performGet()
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "code which contains a couple of bugs")))
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "private void addFiveElements(")));
    }

    @Test
    public void testEval_withInsecureInput_1() throws Exception {
        performEval("System.exit(0);")
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: The solution seems insecure, "
                                                        + "with forbidden keyword: System")));
    }

    @Test
    public void testEval_withInsecureInput_2() throws Exception {
        performEval("Runtime.getRuntime().exec(\\\"rm -rf /\\\");")
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: The solution seems insecure, "
                                                        + "with forbidden keyword: Runtime")));
    }

    @Test
    public void testEval_withImport() throws Exception {
        performEval("import java.util.*;")
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: The code should not contain "
                                                        + "any import statements.")));
    }

    @Test
    public void testEval_withLengthyInput() throws Exception {
        performEval("a".repeat(2001))
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: The solution seems insecure, with its "
                                                        + "length exceeding the max allowable length.")));
    }

    // Also tests for the disallowed changes in the code input.
    @Test
    public void testEval_withBadInput() throws Exception {
        performEval("blahblah")
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: Changes not allowed in "
                                                        + "certain sections of the code.")));
    }

    @Test
    public void testEval_withInitialCodeInput() throws Exception {
        UserInput initialCode = debugListEval.getInitialSolution();
        performEval(initialCode.getMainDefinition())
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: Incorrect solution. "
                                                        + "Fails validation.")));
    }

    @Test
    public void testEval_withTimeout() throws Exception {
        String mainDefinition =
                new TestDataBuilder()
                        .withConstructorDef(CONSTRUCTOR_WITH_TIMEOUT)
                        .build();
        performEval(mainDefinition)
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Server timed out: "
                                                        + "The solution took too long to execute.")));
    }

    @Test
    public void testEval_withWrongAnswer() throws Exception {
        String mainDefinition =
                new TestDataBuilder()
                        .build();
        performEval(mainDefinition)
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: Incorrect solution. "
                                                        + "Fails validation")));
    }

    @Test
    public void testEval_withPartiallyWrongAnswer() throws Exception {
        String mainDefinition =
                new TestDataBuilder()
                        .withAddDef(ADD_WITHOUT_BUG)
                        .withRemoveDef(REMOVE_WITHOUT_BUG)
                        .build();
        performEval(mainDefinition)
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: Getting closer. "
                                                        + "The first iteration is correct,\n"
                                                        + "however the second iteration "
                                                        + "is still wrong.")));
    }

    @Test
    public void testEval_withCompilerFailure() throws Exception {
        String mainDefinition =
                new TestDataBuilder()
                        .withSeriesDef(STATIC_SERIES)
                        .withConstructorDef(CONSTRUCTOR_WITHOUT_SERIES)
                        .withAddDef(ADD_WITHOUT_BUG)
                        .withRemoveDef(REMOVE_WITHOUT_BUG)
                        .build();
        performEval(mainDefinition)
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: Invalid solution.")));
    }

    @Test
    public void testEval_withCorrectAnswer() throws Exception {
        String mainDefinition =
                new TestDataBuilder()
                        .withSeriesDef(STATIC_SERIES)
                        .withConstructorDef(CONSTRUCTOR_WITHOUT_SERIES)
                        .withCreateDef(CREATE_WITH_STATIC)
                        .withAddDef(ADD_WITHOUT_BUG)
                        .withRemoveDef(REMOVE_WITHOUT_BUG)
                        .build();
        performEval(mainDefinition)
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Correct answer: The solution looks correct, "
                                                        + "but your interviewer will be the "
                                                        + "final judge.")));
    }

    private ResultActions performGet() throws Exception {
        return this.mockMvc
                .perform(get(GET_URL))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Debug List")));
    }

    private ResultActions performEval(String mainDefinition) throws Exception {
        return this.mockMvc
                .perform(
                        post(EVAL_URL)
                                .param("main-def-input", mainDefinition)
                                .with(csrf()))
                .andExpect(status().isOk());
    }
}
