package com.oryanend.backend.repositories;

import com.oryanend.backend.entities.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
  Optional<User> findByUsernameIgnoreCase(String username);

  @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))")
  Page<User> searchByUsername(String username, Pageable pageable);

  Optional<User> findByEmail(String email);

  Boolean existsByUsernameIgnoreCase(String username);

  Boolean existsByEmailIgnoreCase(String email);
}
