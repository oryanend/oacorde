package com.oryanend.backend.services.exceptions;

public class DuplicatedFieldException extends RuntimeException {
  public DuplicatedFieldException(String message) {
    super(message);
  }
}
