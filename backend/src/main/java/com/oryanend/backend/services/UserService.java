package com.oryanend.backend.services;

import com.oryanend.backend.entities.User;
import com.oryanend.backend.repositories.UserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  @Autowired private UserRepository userRepository;

  public List<User> findAllUsers() {
    return userRepository.findAll();
  }
}
