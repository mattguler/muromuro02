package com.muromuro.muromuro02.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Runs integration tests for Listing the MuroMuro questions. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WithMockUser("interviewee")
@AutoConfigureMockMvc
public class ListQuestionsIntegrationTest {

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
    public void testListQuestions() throws Exception {
        this.mockMvc
                .perform(get("/muromuro_questions/list"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("The List of Muro Muro Questions")))
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Please select a question here below "
                                                        + "and attempt to answer it.")))
                .andExpect(content().string(containsString("Representing Account States")))
                .andExpect(content().string(containsString("Design API with Pagination")))
                .andExpect(content().string(containsString("Refactoring Too Many Ifs")))
                .andExpect(content().string(containsString("Device Database")))
                .andExpect(content().string(containsString("Detect Substrings")))
                .andExpect(content().string(containsString("Debug List")))
                .andExpect(content().string(containsString("Parse CSV")))
                .andExpect(content().string(containsString("Long Running Functions")))
                .andExpect(content().string(containsString("Incompatible Interfaces")));
    }
}
