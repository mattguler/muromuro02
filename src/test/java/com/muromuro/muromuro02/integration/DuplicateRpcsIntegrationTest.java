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
public class DuplicateRpcsIntegrationTest {

    private static final String GET_URL =
            "/muromuro_questions/duplicate_rpcs";
    private static final String EVAL_URL =
            "/muromuro_questions/eval_duplicate_rpcs";

    private static final String CALLER_CODE_TEMPLATE =
            """
                    public static final class KVListClient<K, V> {
                    
                        private final KVListService<K, V> serviceProxy;
                    
                        public KVListClient(KVListService<K, V> serviceProxy) {
                            this.serviceProxy = serviceProxy;
                        }
                    
                        public List<V> get(K key) {
                            List<V> result = serviceProxy.get(key);
                            return result;
                        }
                    
                        // Append client method.
                        %s
                    
                        public void replaceAll(K key, Iterable<V> values) {
                            serviceProxy.replaceAll(key, values);
                        }
                    
                        public void delete(K key) {
                            serviceProxy.delete(key);
                        }
                    }
                    """;

    private static final String MAIN_DEFINITION_TEMPLATE =
            """
                    public interface KVListService<K, V> {
                        List<V> get(K key);
                        // Append interface definition.
                        %s
                        void replaceAll(K key, Iterable<V> values);
                        void delete(K key);
                    }
                    
                    public static final class KVListServiceImpl<K, V>
                            implements KVListService<K, V> {
                    
                        // Fields definition.
                        %s
                    
                        // Constructor definition.
                        %s
                    
                        @Override
                        public List<V> get(K key) {
                            return db.get(key);
                        }
                    
                        // Append server method.
                        %s
                    
                        @Override
                        public void replaceAll(K key, Iterable<V> values) {
                            db.replaceAll(key, values);
                        }
                    
                        @Override
                        public void delete(K key) {
                            db.delete(key);
                        }
                    }
                    """;

    private static final String DEFAULT_CLIENT_APPEND =
            """
                    public void append(K key, V value) {
                        serviceProxy.append(key, value);
                    }
                    """;

    private static final String MISSING_CLIENT_APPEND =
            """
                    public void append(K key, V value) {
                        //
                    }
                    """;

    private static final String CALL_ID_CLIENT_APPEND =
            """
                    public void append(K key, V value) {
                        String callId = UUID.randomUUID().toString();
                        serviceProxy.append(key, value, callId);
                    }
                    """;

    private static final String DEFAULT_APPEND_INTERFACE =
            """
                    void append(K key, V value);
                    """;

    private static final String CALL_ID_APPEND_INTERFACE =
            """
                    void append(K key, V value, String callId);
                    """;

    private static final String DEFAULT_FIELDS =
            """
                    private final KVListDb<K, V> db;
                    """;

    private static final String CALL_ID_FIELDS =
            """
                    private final KVListDb<K, V> db;
                    private final KVListDb<K, String> callIdDb;
                    """;

    private static final String DEFAULT_CONSTRUCTOR =
            """
                    public KVListServiceImpl(KVListDb<K, V> db) {
                        this.db = db;
                    }
                    """;

    private static final String TIMEOUT_CONSTRUCTOR =
            """
                    public KVListServiceImpl(KVListDb<K, V> db) {
                        this.db = db;
                        while(true) { }
                    }
                    """;

    private static final String CALL_ID_CONSTRUCTOR =
            """
                    public KVListServiceImpl(KVListDb<K, V> db) {
                        this.db = db;
                        callIdDb = KVListDb.createDb();
                    }
                    """;

    private static final String DEFAULT_SERVER_APPEND =
            """
                    @Override
                    public void append(K key, V value) {
                        db.append(key, value);
                    }
                    """;

    private static final String CALL_ID_SERVER_APPEND =
            """
                    @Override
                    public void append(K key, V value, String callId) {
                        List<String> dbValues = callIdDb.get(key);
                        if (dbValues != null && dbValues.size() == 1) {
                            if (callId.equals(dbValues.get(0))) {
                                return;
                            }
                        }
                        db.append(key, value);
                        callIdDb.replaceAll(key, List.of(callId));
                    }
                    """;

    private static String buildCallerCode(String clientAppend) {
        return String.format(CALLER_CODE_TEMPLATE, clientAppend);
    }

    private static String buildMainDefinition(
            String appendInterface,
            String fields,
            String constructor,
            String serverAppend) {
        return String.format(
                MAIN_DEFINITION_TEMPLATE,
                appendInterface,
                fields,
                constructor,
                serverAppend);
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
    public void testGetDuplicateRpcs() throws Exception {
        performGet()
                .validateContains("Duplicate RPCs")
                .validateContains(
                        "Please identify and fix this issue of duplicate RPC requests.");
    }

    @Test
    public void testEval_withInsecureInput_1() throws  Exception {
        performEval("Process rpc;", "")
                .validateContains(
                        "Wrong answer: The solution seems insecure, "
                                + "with forbidden keyword: Process");
    }

    @Test
    public void testEval_withInsecureInput_2() throws  Exception {
        performEval(
                "",
                "Runtime.getRuntime().exec(\"rm -rf /\");")
                .validateContains(
                        "Wrong answer: The solution seems insecure, "
                                + "with forbidden keyword: Runtime");
    }

    @Test
    public void testEval_withImport() throws  Exception {
        performEval("", "import java.util.*;")
                .validateContains(
                        "Wrong answer: The code should not contain "
                                + "any import statements.");
    }

    @Test
    public void testEval_withLengthyInput_1() throws Exception {
        performEval("a".repeat(5001), "")
                .validateContains(
                        "Wrong answer: The solution seems insecure, with its "
                                + "length exceeding the max allowable length.");
    }

    @Test
    public void testEval_withLengthyInput_2() throws Exception {
        performEval("", "a".repeat(5001))
                .validateContains(
                        "Wrong answer: The solution seems insecure, with its "
                                + "length exceeding the max allowable length.");
    }

    @Test
    public void testEval_withBadInput_1() throws Exception {
        String callerCode =
                buildCallerCode(DEFAULT_CLIENT_APPEND);
        String mainDefinition = "blahblah";
        performEval(callerCode, mainDefinition)
                .validateContains("Wrong answer: Invalid solution.");
    }

    @Test
    public void testEval_withBadInput_2() throws Exception {
        String callerCode = "public final class SomeRandomClient { }";
        String mainDefinition =
                buildMainDefinition(
                        DEFAULT_APPEND_INTERFACE,
                        DEFAULT_FIELDS,
                        DEFAULT_CONSTRUCTOR,
                        DEFAULT_SERVER_APPEND);
        performEval(callerCode, mainDefinition)
                .validateContains(
                        "Wrong answer: User input does not contain "
                                + "the correct client name.");
    }

    @Test
    public void testEval_withBadInput_3() throws Exception {
        String callerCode =
                buildCallerCode(MISSING_CLIENT_APPEND);
        String mainDefinition =
                buildMainDefinition(
                        DEFAULT_APPEND_INTERFACE,
                        DEFAULT_FIELDS,
                        DEFAULT_CONSTRUCTOR,
                        DEFAULT_SERVER_APPEND);
        performEval(callerCode, mainDefinition)
                .validateContains(
                        "Wrong answer: User input is missing "
                                + "serviceProxy.append() calls.");
    }

    @Test
    public void testEval_withTimeout() throws Exception {
        String callerCode =
                buildCallerCode(DEFAULT_CLIENT_APPEND);
        String mainDefinition =
                buildMainDefinition(
                        DEFAULT_APPEND_INTERFACE,
                        DEFAULT_FIELDS,
                        TIMEOUT_CONSTRUCTOR,
                        DEFAULT_SERVER_APPEND);
        performEval(callerCode, mainDefinition)
                .validateContains(
                        "Server timed out");
    }

    @Test
    public void testEval_withWrongAnswer() throws Exception {
        String callerCode =
                buildCallerCode(DEFAULT_CLIENT_APPEND);
        String mainDefinition =
                buildMainDefinition(
                        DEFAULT_APPEND_INTERFACE,
                        DEFAULT_FIELDS,
                        DEFAULT_CONSTRUCTOR,
                        DEFAULT_SERVER_APPEND);
        performEval(callerCode, mainDefinition)
                .validateContains(
                        "Wrong answer: Incorrect solution. Fails validation.");
    }

    @Test
    public void testEval_withCorrectAnswer() throws Exception {
        String callerCode =
                buildCallerCode(CALL_ID_CLIENT_APPEND);
        String mainDefinition =
                buildMainDefinition(
                        CALL_ID_APPEND_INTERFACE,
                        CALL_ID_FIELDS,
                        CALL_ID_CONSTRUCTOR,
                        CALL_ID_SERVER_APPEND);
        performEval(callerCode, mainDefinition)
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

    private ResultWrapper performEval(
            String callerCode, String mainDefinition) throws Exception {
        ResultActions resultActions =
                this.mockMvc
                        .perform(
                                post(EVAL_URL)
                                        .param("caller-code-input", callerCode)
                                        .param("main-def-input", mainDefinition)
                                        .with(csrf()))
                        .andExpect(status().isOk());
        return new ResultWrapper(resultActions);
    }

    /** This is here to decrease code clutter in the integration tests. */
    private record ResultWrapper(ResultActions resultActions) {

        ResultWrapper validateContains(String str) throws Exception {
            this.resultActions()
                    .andExpect(content().string(containsString(str)));
            return this;
        }
    }
}
