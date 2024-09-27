package com.muromuro.muromuro02.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Integration tests for the Debug List Muromuro question. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class DebugListIntegrationTest {

    private static final String GET_URL = "/muromuro_questions/debug_list";
    private static final String EVAL_URL = "/muromuro_questions/eval_debug_list";

    private static final String MAIN_DEFINITION_TEMPLATE =
            """ 
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
                    """;

    private static final String NON_STATIC_SERIES =
            "private List<Integer> series;";

    private static final String STATIC_SERIES =
            "private static List<Integer> series = createFiveElements();";

    private static final String CONSTRUCTOR_WITH_SERIES =
            """
                    public DebugListSoln() {
                        series = createFiveElements();
                        addFiveElements(series);
                        removeFirstFiveElements(series);
                    }
                    """;

    private static final String CONSTRUCTOR_WITHOUT_SERIES =
            """
                    public DebugListSoln() {
                        addFiveElements(series);
                        removeFirstFiveElements(series);
                    }
                    """;

    private static final String CONSTRUCTOR_WITH_TIMEOUT =
            """
                    public DebugListSoln() {
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

    @Autowired
    private MockMvc mockMvc;

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

    @Test
    public void testEval_withBadInput() throws Exception {
        performEval("blahblah")
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: Invalid solution.")));
    }

    @Test
    public void testEval_withEmptyInput() throws Exception {
        performEval("")
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Wrong answer: Invalid solution.")));
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
                                .param("userInput.mainDefinition", mainDefinition))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Debug List")));
    }
}
