package com.tiagocosmai.people.security;

import com.tiagocosmai.people.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {

  private final JwtProperties jwtProperties;

  private SecretKey accessKey() {
    return keyFromSecret(jwtProperties.getSecret());
  }

  private SecretKey refreshKey() {
    return keyFromSecret(jwtProperties.getRefreshSecret());
  }

  private static SecretKey keyFromSecret(String secret) {
    try {
      byte[] digest = MessageDigest.getInstance("SHA-256").digest(secret.getBytes(StandardCharsets.UTF_8));
      return Keys.hmacShaKeyFor(digest);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
  }

  public String createAccessToken(String sub, String username, String role) {
    long exp = jwtProperties.getExpirationSeconds();
    return Jwts.builder()
        .subject(sub)
        .claim("username", username)
        .claim("role", role)
        .claim("type", "access")
        .expiration(Date.from(Instant.now().plusSeconds(exp)))
        .signWith(accessKey())
        .compact();
  }

  public String createRefreshToken(String sub, String username, String role) {
    long exp = jwtProperties.getExpirationSeconds();
    return Jwts.builder()
        .subject(sub)
        .claim("username", username)
        .claim("role", role)
        .claim("type", "refresh")
        .expiration(Date.from(Instant.now().plusSeconds(exp)))
        .signWith(refreshKey())
        .compact();
  }

  public Claims parseAccessToken(String token) {
    return Jwts.parser().verifyWith(accessKey()).build().parseSignedClaims(token).getPayload();
  }

  public Claims parseRefreshToken(String token) {
    return Jwts.parser().verifyWith(refreshKey()).build().parseSignedClaims(token).getPayload();
  }
}
