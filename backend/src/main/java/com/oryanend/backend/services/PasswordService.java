package com.oryanend.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordService {
  @Autowired private PasswordEncoder passwordEncoder;

  @Value("${security.pepper:additionalSecretPepper}")
  private String pepper;

  public String encodePassword(String rawPassword) {
    return passwordEncoder.encode(rawPassword + pepper);
  }

  public boolean matches(String rawPassword, String encodedPassword) {
    return passwordEncoder.matches(rawPassword + pepper, encodedPassword);
  }
}
