package com.oryanend.backend.controllers;

import com.oryanend.backend.dto.UserDTO;
import com.oryanend.backend.services.UserService;
import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Controller
@RestController
@RequestMapping(value = "/users")
public class UserController {
  @Autowired private UserService userService;

  @GetMapping
  public ResponseEntity<List<UserDTO>> findAll() {
    List<UserDTO> users = userService.findAllUsers();
    return ResponseEntity.ok().body(users);
  }

  @GetMapping(value = "/{username}")
  public ResponseEntity<UserDTO> findByUsername(@PathVariable String username) {
    UserDTO user = userService.findByUsername(username);
    return ResponseEntity.ok().body(user);
  }

  @PostMapping
  public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO user) {
    UserDTO createdUser = userService.createUser(user);
    URI uri =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(createdUser.getUsername())
            .toUri();
    return ResponseEntity.created(uri).body(createdUser);
  }

  @PatchMapping(value = "/{username}")
  public ResponseEntity<UserDTO> updateUser(
      @PathVariable String username, @RequestBody UserDTO userDTO) {
    UserDTO updatedUser = userService.updateUser(username, userDTO);
    return ResponseEntity.ok().body(updatedUser);
  }

  @DeleteMapping(value = "/{username}")
  public ResponseEntity<Void> deleteUser(@PathVariable String username) {
    userService.deleteUser(username);
    return ResponseEntity.noContent().build();
  }
}
