package com.oryanend.backend.repositories;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.oryanend.backend.entities.User;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
public class UserRepositoryTest {
  @Autowired private UserRepository userRepository;

  @Test
  void findAllShouldReturnListUsers() {
    List<User> userList = userRepository.findAll();
    assertNotNull(userList);
  }
}
