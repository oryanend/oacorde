package com.oryanend.backend.controllers;

import com.oryanend.backend.entities.User;
import com.oryanend.backend.services.UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
@RequestMapping(value = "/users")
public class UserController {
  @Autowired private UserService userService;

  @GetMapping
  public ResponseEntity<List<User>> findAll() {
    List<User> users = userService.findAllUsers();
    return ResponseEntity.ok().body(users);
  }
}
