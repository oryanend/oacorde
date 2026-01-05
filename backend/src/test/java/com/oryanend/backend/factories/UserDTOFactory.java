package com.oryanend.backend.factories;

import com.oryanend.backend.dto.UserDTO;
import com.oryanend.backend.entities.User;
import net.datafaker.Faker;

public class UserDTOFactory {
  private static final Faker faker = new Faker();

  public static UserDTO createUserDTO(String username, String email, String password) {
    User user =
        new User(
            null,
            username != null ? username : faker.name().firstName(),
            email != null ? email : faker.internet().emailAddress(),
            password != null ? password : faker.text().text());
    return new UserDTO(user);
  }

  public static UserDTO createUserDTO() {
    return createUserDTO(null, null, null);
  }
}
