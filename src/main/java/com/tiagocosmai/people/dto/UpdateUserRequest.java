package com.tiagocosmai.people.dto;

import com.tiagocosmai.people.domain.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {

  @Size(min = 3)
  private String username;

  @Email
  private String email;

  @Size(min = 8)
  private String password;

  private Role role;
}
