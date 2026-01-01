package com.oryanend.backend.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
  @Autowired private MockMvc mockMvc;

  @Test
  @DisplayName("GET /users should return list of users")
  public void test() throws Exception {
    ResultActions result = mockMvc.perform(get("/users").accept(MediaType.APPLICATION_JSON));

    result
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(3))
        .andExpect(jsonPath("$[0].id").isNotEmpty())
        .andExpect(jsonPath("$[0].username").isNotEmpty())
        .andExpect(jsonPath("$[0].email").isNotEmpty())
        .andExpect(jsonPath("$[0].password").isNotEmpty());
  }
}
