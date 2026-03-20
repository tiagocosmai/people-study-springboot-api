package com.tiagocosmai.people.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {

  @NotBlank
  @JsonProperty("refreshToken")
  private String refreshToken;
}
