package com.tiagocosmai.people.dto;

import com.tiagocosmai.people.domain.Role;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
  private String id;
  private String username;
  private String email;
  private Role role;
  private Instant createdAt;
  private Instant updatedAt;
}
