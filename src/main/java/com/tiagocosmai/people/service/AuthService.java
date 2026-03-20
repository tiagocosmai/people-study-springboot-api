package com.tiagocosmai.people.service;

import com.tiagocosmai.people.domain.User;
import com.tiagocosmai.people.dto.AuthResponse;
import com.tiagocosmai.people.dto.LoginRequest;
import com.tiagocosmai.people.dto.MessageResponse;
import com.tiagocosmai.people.repository.UserRepository;
import com.tiagocosmai.people.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public AuthResponse login(LoginRequest req) {
    User user =
        userRepository
            .findByUsername(req.getUsername())
            .filter(u -> passwordEncoder.matches(req.getPassword(), u.getPassword()))
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
    return issueTokens(user);
  }

  public AuthResponse refresh(String refreshToken) {
    if (refreshToken == null || refreshToken.isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refresh token is required");
    }
    try {
      Claims claims = jwtService.parseRefreshToken(refreshToken);
      if (!"refresh".equals(claims.get("type"))) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token type");
      }
      User user =
          userRepository
              .findById(claims.getSubject())
              .filter(u -> refreshToken.equals(u.getRefreshToken()))
              .orElseThrow(
                  () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));
      return issueTokens(user);
    } catch (JwtException e) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired refresh token");
    }
  }

  private AuthResponse issueTokens(User user) {
    long expiresIn = 3600;
    String access =
        jwtService.createAccessToken(user.getId(), user.getUsername(), user.getRole().name());
    String refresh =
        jwtService.createRefreshToken(user.getId(), user.getUsername(), user.getRole().name());
    user.setRefreshToken(refresh);
    userRepository.save(user);
    return new AuthResponse(access, refresh, expiresIn);
  }

  public MessageResponse forgotPassword(String email) {
    userRepository.findByEmail(email);
    return new MessageResponse(
        "If the email exists, a password reset link will be sent. Check your inbox.");
  }
}
