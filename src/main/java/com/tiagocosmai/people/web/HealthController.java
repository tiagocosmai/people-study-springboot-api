package com.tiagocosmai.people.web;

import com.tiagocosmai.people.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Health")
@RestController
@RequiredArgsConstructor
public class HealthController {

  private final UserRepository userRepository;

  @Operation(summary = "Complete health check")
  @GetMapping({"/health-check", "/health"})
  public ResponseEntity<Map<String, Object>> check() {
    try {
      userRepository.count();
      Map<String, Object> db = Map.of("status", "up");
      Map<String, Object> body = new LinkedHashMap<>();
      body.put("status", "ok");
      body.put("info", Map.of("database", db));
      body.put("error", Map.of());
      body.put("details", Map.of("database", db));
      return ResponseEntity.ok(body);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }
  }
}
