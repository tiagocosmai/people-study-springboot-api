package com.tiagocosmai.people.service;

import com.tiagocosmai.people.domain.Role;
import com.tiagocosmai.people.domain.User;
import com.tiagocosmai.people.dto.CreateUserRequest;
import com.tiagocosmai.people.dto.UpdateUserRequest;
import com.tiagocosmai.people.dto.UserResponse;
import com.tiagocosmai.people.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final MongoTemplate mongoTemplate;

  public UserResponse create(CreateUserRequest req) {
    if (userRepository.findByUsername(req.getUsername()).isPresent()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
    }
    if (userRepository.findByEmail(req.getEmail()).isPresent()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
    }
    User user =
        User.builder()
            .username(req.getUsername())
            .email(req.getEmail())
            .password(passwordEncoder.encode(req.getPassword()))
            .role(req.getRole())
            .build();
    return toResponse(userRepository.save(user));
  }

  public List<UserResponse> findAll(String search) {
    Query query = new Query().with(Sort.by(Sort.Direction.DESC, "createdAt"));
    if (search != null && !search.isBlank()) {
      String term = search.trim();
      List<Criteria> ors = new ArrayList<>();
      ors.add(Criteria.where("username").regex(Pattern.quote(term), "i"));
      ors.add(Criteria.where("email").regex(Pattern.quote(term), "i"));
      String up = term.toUpperCase();
      for (Role r : Role.values()) {
        if (r.name().equals(up)) {
          ors.add(Criteria.where("role").is(r.name()));
          break;
        }
      }
      query.addCriteria(new Criteria().orOperator(ors.toArray(new Criteria[0])));
    }
    return mongoTemplate.find(query, User.class).stream().map(this::toResponse).toList();
  }

  public UserResponse findOne(String id) {
    return userRepository
        .findById(id)
        .map(this::toResponse)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with ID " + id + " not found"));
  }

  public UserResponse update(String id, UpdateUserRequest req) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with ID " + id + " not found"));

    if (req.getUsername() != null && !req.getUsername().isBlank()) {
      userRepository
          .findByUsername(req.getUsername())
          .filter(u -> !u.getId().equals(id))
          .ifPresent(
              u -> {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
              });
      user.setUsername(req.getUsername());
    }
    if (req.getEmail() != null && !req.getEmail().isBlank()) {
      userRepository
          .findByEmail(req.getEmail())
          .filter(u -> !u.getId().equals(id))
          .ifPresent(
              u -> {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
              });
      user.setEmail(req.getEmail());
    }
    if (req.getPassword() != null && !req.getPassword().isBlank()) {
      user.setPassword(passwordEncoder.encode(req.getPassword()));
    }
    if (req.getRole() != null) {
      user.setRole(req.getRole());
    }
    return toResponse(userRepository.save(user));
  }

  public void remove(String id) {
    if (!userRepository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with ID " + id + " not found");
    }
    userRepository.deleteById(id);
  }

  private UserResponse toResponse(User u) {
    return UserResponse.builder()
        .id(u.getId())
        .username(u.getUsername())
        .email(u.getEmail())
        .role(u.getRole())
        .createdAt(u.getCreatedAt())
        .updatedAt(u.getUpdatedAt())
        .build();
  }
}
