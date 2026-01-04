package com.oryanend.backend.controllers;

import static com.oryanend.backend.factories.UserDTOFactory.createUserDTO;
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
  private String nonExistingUsername;
  private User userTest;
  private UserDTO userTestDTO;

  @BeforeEach
  void setUp() throws Exception {
    userName = "testuser";
    userEmail = "email@test.com";
    userPassword = "testpassword";

    userTest = new User(null, userName, userEmail, passwordService.encodePassword(userPassword));
    userTestDTO = new UserDTO(userTest);

    nonExistingUsername = "nonexistinguser";
  }

  @Test
  @DisplayName("GET `/users` should return list of users")
  void getUsers() throws Exception {
    ResultActions result = mockMvc.perform(get(baseUrl).accept(MediaType.APPLICATION_JSON));

    result
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].username").isNotEmpty())
        .andExpect(jsonPath("$[0].email").isNotEmpty())
        .andExpect(jsonPath("$[0].password").isNotEmpty());
  }

  @Test
  @DisplayName("GET `/users/{username}` should return user by username")
  void getUserByUsername() throws Exception {
    String jsonBody = objectMapper.writeValueAsString(userTestDTO);

    mockMvc.perform(
        post(baseUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonBody)
            .accept(MediaType.APPLICATION_JSON));
    ResultActions result =
        mockMvc.perform(get(baseUrl + "/" + userName).accept(MediaType.APPLICATION_JSON));

    result
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value(userName))
        .andExpect(jsonPath("$.email").value(userEmail))
        .andExpect(jsonPath("$.password").isNotEmpty());
  }

  @Test
  @DisplayName("GET `/users/{username}` should return 404 for non-existing user")
  void getUserByUsernameNotFound() throws Exception {
    ResultActions result =
        mockMvc.perform(
            get(baseUrl + "/" + nonExistingUsername).accept(MediaType.APPLICATION_JSON));

    result
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(
            jsonPath("$.error")
                .value("Doesn't exist any user with this username, try another one."))
        .andExpect(jsonPath("$.path").value(baseUrl + "/" + nonExistingUsername));
  }

  @Test
  @DisplayName("POST `/users` should create a user and return 201")
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

  @Test
  @DisplayName("POST `/users` should return 400 for duplicate `username`")
  void postUserDuplicateUsername() throws Exception {
    // First attempt to create user
    String jsonBody =
        objectMapper.writeValueAsString(createUserDTO("duplicatedUsername", null, null));
    ResultActions resultFirstPost =
        mockMvc.perform(
            post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .accept(MediaType.APPLICATION_JSON));
    resultFirstPost.andExpect(status().isCreated());

    // Second attempt to create user with same username
    jsonBody = objectMapper.writeValueAsString(createUserDTO("DuplicatedUsername", null, null));
    ResultActions resultSecondPost =
        mockMvc.perform(
            post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .accept(MediaType.APPLICATION_JSON));
    resultSecondPost
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("This username is already taken, try other username."))
        .andExpect(jsonPath("$.path").value(baseUrl));
  }

  @Test
  @DisplayName("POST `/users` should return 400 for duplicate `email`")
  void postUserDuplicateEmail() throws Exception {
    // First attempt to create user
    String jsonBody =
        objectMapper.writeValueAsString(createUserDTO(null, "email-duplicated@gmail.com", null));
    ResultActions resultFirstPost =
        mockMvc.perform(
            post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .accept(MediaType.APPLICATION_JSON));
    resultFirstPost.andExpect(status().isCreated());

    // Second attempt to create user with same username
    jsonBody =
        objectMapper.writeValueAsString(createUserDTO(null, "Email-Duplicated@gmail.com", null));
    ResultActions resultSecondPost =
        mockMvc.perform(
            post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .accept(MediaType.APPLICATION_JSON));
    resultSecondPost
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("This email is already taken, try other email."))
        .andExpect(jsonPath("$.path").value(baseUrl));
  }
}
