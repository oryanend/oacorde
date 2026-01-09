package com.oryanend.backend.controllers;

import com.oryanend.backend.dto.UserDTO;
import com.oryanend.backend.dto.UserMinDTO;
import com.oryanend.backend.services.UserService;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Controller
@RestController
@RequestMapping(value = "api/v1/users")
public class UserController {
  @Autowired private UserService userService;

  @GetMapping
  public ResponseEntity<Page<UserMinDTO>> findAll(
      @RequestParam(value = "username", defaultValue = "") String username, Pageable pageable) {
    Page<UserMinDTO> dto = userService.findAllUsers(username, pageable);
    return ResponseEntity.ok().body(dto);
  }

  public ResponseEntity<Page<UserMinDTO>> findAll(Pageable pageable) {
    Page<UserMinDTO> dto = userService.findAllUsers("", pageable);
    return ResponseEntity.ok().body(dto);
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
