package com.oryanend.backend.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.oryanend.backend.entities.User;
import java.time.Instant;

@JsonPropertyOrder({"username", "createdAt", "updatedAt"})
public class UserMinDTO {
  private String username;
  private Instant createdAt;
  private Instant updatedAt;

  public UserMinDTO(String username, Instant createdAt, Instant updatedAt) {
    this.username = username;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public UserMinDTO(User entity) {
    this.username = entity.getUsername();
    this.createdAt = entity.getCreatedAt();
    this.updatedAt = entity.getUpdatedAt();
  }

  public String getUsername() {
    return username;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
