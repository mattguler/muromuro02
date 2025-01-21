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

/** Integration tests for the Parse CSV Muromuro question. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WithMockUser("interviewee")
@AutoConfigureMockMvc
public class ParseCsvIntegrationTest {

    private static final String GET_URL = "/muromuro_questions/parse_csv";
    private static final String EVAL_URL = "/muromuro_questions/eval_parse_csv";

    private static final String CORRECT_SOLUTION =
            """
                    public List<Account> parseCsv(String csv) {
                        List<Account> results = new ArrayList<>();
                        String[] lines = csv.split("\\n");
                        List<String> titles = new ArrayList<>();
                        for (int i = 0; i < lines.length; i++) {
                            if (i == 0) {
                                String[] titlesArray = lines[i].split(",");
                                titles =
                                        Arrays
                                                .stream(titlesArray)
                                                .map(String::strip)
                                                .collect(Collectors.toList());
                                continue;
                            }
                            Account account = parseLine(lines[i], titles);
                            results.add(account);
                        }
                        return results;
                    }
                    
                    private Account parseLine(String line, List<String> titles) {
                        int id = 0;
                        String name = "";
                        AccountState state = AccountState.UNDEFINED;
                        int age = 0;
                        String firstName = "";
                        String lastName = "";
                        // The -5 limit makes sure the trailing empty strings are kept.
                        String[] cells = line.split(",", -5);
                        for (int i = 0; i < cells.length; i++) {
                            String cell = cells[i].strip();
                            switch(titles.get(i)) {
                                case "id":
                                    id = cell.isEmpty() ? 0 : Integer.parseInt(cell);
                                    break;
                                case "account_name":
                                    name = cell;
                                    break;
                                case "account_state":
                                    state = parseAccountState(cell);
                                    break;
                                case "account_age":
                                    age = cell.isEmpty() ? 0 : Integer.parseInt(cell);
                                    break;
                                case "owner_first_name":
                                    firstName = cell;
                                    break;
                                case "owner_last_name":
                                    lastName = cell;
                                    break;
                                default:
                                    break;
                            }
                        }
                        Account account =
                                new Account(id, name, state, age, firstName, lastName);
                        return account;
                    }
                    
                    private AccountState parseAccountState(String cell) {
                        return switch(cell) {
                            case "ACTIVE" -> AccountState.ACTIVE;
                            case "INACTIVE" -> AccountState.INACTIVE;
                            default -> AccountState.UNDEFINED;
                        };
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
    public void testGetParseCsv() throws Exception {
        performGet()
                .validateContains("Parse CSV")
                .validateContains(
                        "You are given a comma separated value (CSV) file string")
                .validateContains(
                        "Here are a few caveats and important points");
    }

    @Test
    public void testEval_withInsecureInput_1() throws Exception {
        performEval("System.exit(0);")
                .validateContains(
                        "Wrong answer: The solution seems insecure, "
                                + "with forbidden keyword: System");
    }

    @Test
    public void testEval_withInsecureInput_2() throws Exception {
        performEval("Runtime.getRuntime().exec(\\\"rm -rf /\\\");")
                .validateContains(
                        "Wrong answer: The solution seems insecure, "
                                + "with forbidden keyword: Runtime");
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
        performEval("a".repeat(4001))
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
    public void testEval_withNullPointerError() throws Exception {
        String input =
                """
                        public List<Account> parseCsv(String csv) {
                            Account account = null;
                            int id = account.id();
                            return List.of();
                        }
                        """;
        performEval(input)
                .validateContains("Wrong answer: Invalid solution.");
    }

    @Test
    public void testEval_withTimeout() throws Exception {
        String input =
                """
                        public List<Account> parseCsv(String csv) {
                            int a = 5;
                            while (a != 0) {
                                a = -a;
                            }
                            return List.of();
                        }
                        """;
        performEval(input)
                .validateContains(
                        "Server timed out: The solution took too long to execute.");
    }

    @Test
    public void testEval_withWrongAnswer() throws Exception {
        String input =
                """
                        public List<Account> parseCsv(String csv) {
                            return List.of();
                        }
                        """;
        performEval(input)
                .validateContains("Wrong answer: Incorrect solution. Fails validation");
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
