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
}
