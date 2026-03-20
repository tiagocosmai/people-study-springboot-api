package com.tiagocosmai.people.bootstrap;

import com.tiagocosmai.people.domain.Role;
import com.tiagocosmai.people.domain.User;
import com.tiagocosmai.people.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) {
    String hash = passwordEncoder.encode("Admin@123");
    ensureUser("admin", "admin@people-api.com", Role.ADMIN, hash);
    ensureUser("system", "system@people-api.com", Role.SYSTEM, hash);
    ensureUser("viewer", "viewer@people-api.com", Role.VIEWER, hash);
  }

  private void ensureUser(String username, String email, Role role, String passwordHash) {
    if (userRepository.findByUsername(username).isPresent()) {
      return;
    }
    userRepository.save(
        User.builder().username(username).email(email).password(passwordHash).role(role).build());
  }
}
