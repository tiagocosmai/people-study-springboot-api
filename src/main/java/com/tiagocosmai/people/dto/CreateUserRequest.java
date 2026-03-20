package com.tiagocosmai.people.dto;

import com.tiagocosmai.people.domain.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequest {

  @NotBlank @Size(min = 3)
  private String username;

  @NotBlank @Email
  private String email;

  @NotBlank @Size(min = 8)
  private String password;

  @NotNull private Role role;
}
