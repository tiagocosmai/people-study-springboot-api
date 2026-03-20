package com.tiagocosmai.people.web;

import com.tiagocosmai.people.dto.AuthResponse;
import com.tiagocosmai.people.dto.ForgotPasswordRequest;
import com.tiagocosmai.people.dto.LoginRequest;
import com.tiagocosmai.people.dto.MessageResponse;
import com.tiagocosmai.people.dto.RefreshTokenRequest;
import com.tiagocosmai.people.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @Operation(summary = "Login with username and password")
  @PostMapping("/login")
  public AuthResponse login(@Valid @RequestBody LoginRequest body) {
    return authService.login(body);
  }

  @Operation(summary = "Refresh access token using refresh token")
  @PostMapping("/refresh")
  public AuthResponse refresh(@Valid @RequestBody RefreshTokenRequest body) {
    return authService.refresh(body.getRefreshToken());
  }

  @Operation(summary = "Request password reset")
  @PostMapping("/forgot-password")
  public MessageResponse forgotPassword(@Valid @RequestBody ForgotPasswordRequest body) {
    return authService.forgotPassword(body.getEmail());
  }
}
