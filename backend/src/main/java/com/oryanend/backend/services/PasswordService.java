package com.oryanend.backend.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordService {

  private final PasswordEncoder passwordEncoder;
  private final MessageDigest sha256;

  @Value("${security.pepper}")
  private String pepper;

  public PasswordService(PasswordEncoder passwordEncoder) throws NoSuchAlgorithmException {
    this.passwordEncoder = passwordEncoder;
    this.sha256 = MessageDigest.getInstance("SHA-256");
  }

  public String encodePassword(String rawPassword) {
    String preHashed = preHash(rawPassword);
    return passwordEncoder.encode(preHashed);
  }

  public boolean matches(String rawPassword, String encodedPassword) {
    String preHashed = preHash(rawPassword);
    return passwordEncoder.matches(preHashed, encodedPassword);
  }

  private String preHash(String rawPassword) {
    byte[] hash = sha256.digest((rawPassword + pepper).getBytes(StandardCharsets.UTF_8));
    return Base64.getEncoder().encodeToString(hash);
  }
}
