package com.oryanend.backend.services;

import com.oryanend.backend.dto.UserDTO;
import com.oryanend.backend.entities.User;
import com.oryanend.backend.repositories.UserRepository;
import com.oryanend.backend.services.exceptions.DuplicatedFieldException;
import com.oryanend.backend.services.exceptions.ResourceNotFoundException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
  public UserDTO findByUsername(String username) {
    User user =
        userRepository
            .findByUsernameContainingIgnoreCase(username)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Doesn't exist any user with this username, try another one."));
    return new UserDTO(user);
  }

  @Transactional
  public UserDTO createUser(UserDTO user) {
    if (userRepository.existsByUsernameIgnoreCase(user.getUsername())) {
      throw new DuplicatedFieldException("This username is already taken, try other username.");
    }

    if (userRepository.existsByEmailIgnoreCase(user.getEmail())) {
      throw new DuplicatedFieldException("This email is already taken, try other email.");
    }

    user.setPassword(passwordService.encodePassword(user.getPassword()));
    User entity = new User();
    copyEntityToDTO(user, entity);

    return new UserDTO(userRepository.save(entity));
  }

  @Transactional
  public UserDTO updateUser(String username, UserDTO userDTO) {
    if (userRepository.existsByUsernameIgnoreCase(userDTO.getUsername())) {
      throw new DuplicatedFieldException("This username is already taken, try other username.");
    }

    if (userRepository.existsByEmailIgnoreCase(userDTO.getEmail())) {
      throw new DuplicatedFieldException("This email is already taken, try other email.");
    }

    User user =
        userRepository
            .findByUsernameContainingIgnoreCase(username)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Doesn't exist any user with this username, try another one."));
    copyEntityToDTO(userDTO, user);

    return new UserDTO(userRepository.save(user));
  }

  @Transactional(propagation = Propagation.SUPPORTS)
  public void deleteUser(String username) {
    User user =
        userRepository
            .findByUsernameContainingIgnoreCase(username)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Doesn't exist any user with this username, try another one."));
    userRepository.delete(user);
  }

  private void copyEntityToDTO(UserDTO dto, User entity) {
    if (dto.getEmail() != null) {
      entity.setEmail(dto.getEmail());
    }
    if (dto.getPassword() != null) {
      entity.setPassword(passwordService.encodePassword(dto.getPassword()));
    }
    if (dto.getUsername() != null) {
      entity.setUsername(dto.getUsername());
    }
  }
}
