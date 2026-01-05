package com.oryanend.backend.controllers.handlers;

import com.oryanend.backend.dto.CustomErrorDTO;
import com.oryanend.backend.services.exceptions.DuplicatedFieldException;
import com.oryanend.backend.services.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExeceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<CustomErrorDTO> resourceNotFound(
      ResourceNotFoundException e, HttpServletRequest request) {
    HttpStatus status = HttpStatus.NOT_FOUND;
    CustomErrorDTO err =
        new CustomErrorDTO(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
    return ResponseEntity.status(status).body(err);
  }

  @ExceptionHandler(DuplicatedFieldException.class)
  public ResponseEntity<CustomErrorDTO> duplicatedField(Exception e, HttpServletRequest request) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    CustomErrorDTO err =
        new CustomErrorDTO(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
    return ResponseEntity.status(status).body(err);
  }
}
