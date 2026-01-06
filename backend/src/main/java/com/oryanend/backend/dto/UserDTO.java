package com.oryanend.backend.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.oryanend.backend.entities.User;
import java.time.Instant;

@JsonPropertyOrder({"username", "email", "password", "createdAt", "updatedAt"})
public class UserDTO {
  private String username;
  private String email;
  private String password;
  private Instant createdAt;
  private Instant updatedAt;

  public UserDTO(
      String username, String email, String password, Instant createdAt, Instant updatedAt) {
    this.username = username;
    this.email = email;
    this.password = password;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public UserDTO(User entity) {
    this.username = entity.getUsername();
    this.email = entity.getEmail();
    this.password = entity.getPassword();
    this.createdAt = entity.getCreatedAt();
    this.updatedAt = entity.getUpdatedAt();
  }

  public UserDTO() {}

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
