package com.oryanend.backend.controllers;

import static com.oryanend.backend.factories.UserDTOFactory.createUserDTO;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.oryanend.backend.dto.UserDTO;
import com.oryanend.backend.entities.User;
import com.oryanend.backend.repositories.UserRepository;
import com.oryanend.backend.services.PasswordService;
import java.util.HashMap;
import java.util.Map;
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
  @Autowired private UserRepository userRepository;

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

  @Test
  @DisplayName("PATCH `/users/{username}` should return 404 for non-existing user")
  void patchUserWithUsernameNotFound() throws Exception {
    String jsonBody = objectMapper.writeValueAsString(userTestDTO);

    ResultActions result =
        mockMvc.perform(
            patch(baseUrl + "/" + nonExistingUsername)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .accept(MediaType.APPLICATION_JSON));

    result
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(
            jsonPath("$.error")
                .value("Doesn't exist any user with this username, try another one."))
        .andExpect(jsonPath("$.path").value(baseUrl + "/" + nonExistingUsername));
  }

  @Test
  @DisplayName("PATCH `/users/{username}` should return 200 when password is updated")
  void patchUserWithCorrectPassword() throws Exception {
    UserDTO updatedUserDTO = createUserDTO();

    String jsonBody = objectMapper.writeValueAsString(updatedUserDTO);
    ResultActions resultFirstPost =
        mockMvc.perform(
            post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .accept(MediaType.APPLICATION_JSON));
    resultFirstPost.andExpect(status().isCreated());

    Map<String, String> updateFields = new HashMap<>();
    updateFields.put("password", "novasenha123");

    jsonBody = objectMapper.writeValueAsString(updateFields);

    ResultActions result =
        mockMvc.perform(
            patch(baseUrl + "/" + updatedUserDTO.getUsername())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .accept(MediaType.APPLICATION_JSON));

    result
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value(updatedUserDTO.getUsername()))
        .andExpect(jsonPath("$.email").value(updatedUserDTO.getEmail()))
        .andExpect(jsonPath("$.password").isNotEmpty());

    User updatedUser =
        userRepository
            .findByUsernameContainingIgnoreCase(updatedUserDTO.getUsername())
            .orElseThrow();

    // Verify password encoded matches with the original password
    assert passwordService.matches(updateFields.get("password"), updatedUser.getPassword());
  }

  @Test
  @DisplayName("PATCH `/users/{username}` should return 200 when email is updated")
  void patchUserWithCorrectEmail() throws Exception {
    UserDTO updatedUserDTO = createUserDTO();

    String jsonBody = objectMapper.writeValueAsString(updatedUserDTO);
    ResultActions resultFirstPost =
        mockMvc.perform(
            post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .accept(MediaType.APPLICATION_JSON));
    resultFirstPost.andExpect(status().isCreated());

    Map<String, String> updateFields = new HashMap<>();
    updateFields.put("email", "email@uptaded.com");

    jsonBody = objectMapper.writeValueAsString(updateFields);

    ResultActions result =
        mockMvc.perform(
            patch(baseUrl + "/" + updatedUserDTO.getUsername())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .accept(MediaType.APPLICATION_JSON));

    result
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value(updatedUserDTO.getUsername()))
        .andExpect(jsonPath("$.email").value(updateFields.get("email")))
        .andExpect(jsonPath("$.password").isNotEmpty());
  }

  @Test
  @DisplayName("PATCH `/users/{username}` should return 200 when username is updated")
  void patchUserWithCorrectUsername() throws Exception {
    UserDTO updatedUserDTO = createUserDTO();

    String jsonBody = objectMapper.writeValueAsString(updatedUserDTO);
    ResultActions resultFirstPost =
        mockMvc.perform(
            post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .accept(MediaType.APPLICATION_JSON));
    resultFirstPost.andExpect(status().isCreated());

    Map<String, String> updateFields = new HashMap<>();
    updateFields.put("username", "usernameUpdated");

    jsonBody = objectMapper.writeValueAsString(updateFields);

    ResultActions result =
        mockMvc.perform(
            patch(baseUrl + "/" + updatedUserDTO.getUsername())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .accept(MediaType.APPLICATION_JSON));

    result
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value(updateFields.get("username")))
        .andExpect(jsonPath("$.email").value(updatedUserDTO.getEmail()))
        .andExpect(jsonPath("$.password").isNotEmpty());
  }

  @Test
  @DisplayName("PATCH `/users/{username}` should return 400 for duplicate `username`")
  void patchUserDuplicateUsername() throws Exception {
    // First user creation
    UserDTO firstUserDTO = createUserDTO("firstUser", null, null);

    String jsonBody = objectMapper.writeValueAsString(firstUserDTO);
    ResultActions resultFirstPost =
        mockMvc.perform(
            post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .accept(MediaType.APPLICATION_JSON));

    resultFirstPost.andExpect(status().isCreated());

    // Second user creation
    UserDTO secondUserDTO = createUserDTO("secondUser", null, null);

    jsonBody = objectMapper.writeValueAsString(secondUserDTO);
    ResultActions resultSecondPost =
        mockMvc.perform(
            post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .accept(MediaType.APPLICATION_JSON));

    resultSecondPost.andExpect(status().isCreated());

    // Attempt to update second user's username to first user's username
    Map<String, String> updateFields = new HashMap<>();
    updateFields.put("username", "FirstUser");

    jsonBody = objectMapper.writeValueAsString(updateFields);
    ResultActions result =
        mockMvc.perform(
            patch(baseUrl + "/" + secondUserDTO.getUsername())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .accept(MediaType.APPLICATION_JSON));

    result
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("This username is already taken, try other username."))
        .andExpect(jsonPath("$.path").value(baseUrl + "/" + secondUserDTO.getUsername()));
  }

  @Test
  @DisplayName("PATCH `/users/{username}` should return 400 for duplicate `username`")
  void patchUserDuplicateEmail() throws Exception {
    // First user creation
    UserDTO firstUserDTO = createUserDTO(null, "first@email.com", null);

    String jsonBody = objectMapper.writeValueAsString(firstUserDTO);
    ResultActions resultFirstPost =
        mockMvc.perform(
            post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .accept(MediaType.APPLICATION_JSON));

    resultFirstPost.andExpect(status().isCreated());

    // Second user creation
    UserDTO secondUserDTO = createUserDTO(null, "second@email.com", null);

    jsonBody = objectMapper.writeValueAsString(secondUserDTO);
    ResultActions resultSecondPost =
        mockMvc.perform(
            post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .accept(MediaType.APPLICATION_JSON));

    resultSecondPost.andExpect(status().isCreated());

    // Attempt to update second user's email to first user's email
    Map<String, String> updateFields = new HashMap<>();
    updateFields.put("email", "first@email.com");

    jsonBody = objectMapper.writeValueAsString(updateFields);
    ResultActions result =
        mockMvc.perform(
            patch(baseUrl + "/" + secondUserDTO.getUsername())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .accept(MediaType.APPLICATION_JSON));

    result
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("This email is already taken, try other email."))
        .andExpect(jsonPath("$.path").value(baseUrl + "/" + secondUserDTO.getUsername()));
  }

  @Test
  @DisplayName("DELETE `/users/{username}` should return 404 for non-existing user")
  void deleteUserByUsernameNotFound() throws Exception {
    ResultActions result =
        mockMvc.perform(
            delete(baseUrl + "/" + nonExistingUsername).accept(MediaType.APPLICATION_JSON));

    result
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(
            jsonPath("$.error")
                .value("Doesn't exist any user with this username, try another one."))
        .andExpect(jsonPath("$.path").value(baseUrl + "/" + nonExistingUsername));
  }

  @Test
  @DisplayName("DELETE `/users/{username}` should delete user by username")
  void deleteUserByUsername() throws Exception {
    String jsonBody = objectMapper.writeValueAsString(userTestDTO);

    mockMvc.perform(
        post(baseUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonBody)
            .accept(MediaType.APPLICATION_JSON));
    ResultActions result =
        mockMvc.perform(delete(baseUrl + "/" + userName).accept(MediaType.APPLICATION_JSON));

    result.andExpect(status().isNoContent());

    assert !(userRepository.existsByUsernameIgnoreCase(userName));
  }
  // Needed to Tests that throw error when existsByUsernameIgnoreCase return more than one user.
}
