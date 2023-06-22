package com.muromuro.muromuro02.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Runs integration tests for the Design API with Pagination Muromuro question. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class DesignApiWithPaginationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetDesignApiWithPagination() throws Exception {
        this.mockMvc
                .perform(get("/muromuro_questions/design_api_with_pagination"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Design API with Pagination")))
                .andExpect(
                        content()
                                .string(
                                        containsString(
                                                "Please implement the call to your API function "
                                                        + "and your actual API function definition")));

    }
}
