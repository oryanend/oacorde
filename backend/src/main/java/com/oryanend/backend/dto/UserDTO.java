package com.oryanend.backend.dto;

import com.oryanend.backend.entities.User;

public class UserDTO {
  private String username;
  private String email;
  private String password;

  public UserDTO(String username, String email, String password) {
    this.username = username;
    this.email = email;
    this.password = password;
  }

  public UserDTO(User entity) {
    this.username = entity.getUsername();
    this.email = entity.getEmail();
    this.password = entity.getPassword();
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
}
