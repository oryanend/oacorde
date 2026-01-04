package com.oryanend.backend.services;

import com.oryanend.backend.dto.UserDTO;
import com.oryanend.backend.entities.User;
import com.oryanend.backend.repositories.UserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
  @Autowired private UserRepository userRepository;
  @Autowired private PasswordService passwordService;

  @Transactional(readOnly = true)
  public List<UserDTO> findAllUsers() {
    return userRepository.findAll().stream().map(UserDTO::new).toList();
  }

  @Transactional(readOnly = true)
  public UserDTO createUser(UserDTO user) {
    user.setPassword(passwordService.encodePassword(user.getPassword()));
    User entity = new User();
    copyEntityToDTO(user, entity);

    return new UserDTO(userRepository.save(entity));
  }

  private void copyEntityToDTO(UserDTO dto, User entity) {
    entity.setUsername(dto.getUsername());
    entity.setEmail(dto.getEmail());
    entity.setPassword(dto.getPassword());
  }
}
