package com.oryanend.backend.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.oryanend.backend.dto.UserDTO;
import com.oryanend.backend.entities.User;
import com.oryanend.backend.services.PasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerTest {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Autowired private PasswordService passwordService;

  private static final String baseUrl = "/users";
  private String userName, userEmail, userPassword;
  private User userTest;
  private UserDTO userTestDTO;

  @BeforeEach
  void setUp() throws Exception {
    userName = "testuser";
    userEmail = "email@test.com";
    userPassword = "testpassword";

    userTest = new User(null, userName, userEmail, passwordService.encodePassword(userPassword));
    userTestDTO = new UserDTO(userTest);
  }

  @Test
  @DisplayName("GET /users should return list of users")
  public void getUsers() throws Exception {
    ResultActions result = mockMvc.perform(get(baseUrl).accept(MediaType.APPLICATION_JSON));

    result
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].username").isNotEmpty())
        .andExpect(jsonPath("$[0].email").isNotEmpty())
        .andExpect(jsonPath("$[0].password").isNotEmpty());
  }

  @Test
  @DisplayName("POST /users should create a user and return 201")
  void postUser() throws Exception {
    String jsonBody = objectMapper.writeValueAsString(userTestDTO);

    ResultActions result =
        mockMvc.perform(
            post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .accept(MediaType.APPLICATION_JSON));

    result
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value(userName))
        .andExpect(jsonPath("$.email").value(userEmail))
        .andExpect(jsonPath("$.password").isNotEmpty());

    // Verify password encoded matches with the original password
    assert passwordService.matches(userPassword, userTest.getPassword());
  }
}
