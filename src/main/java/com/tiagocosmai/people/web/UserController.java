package com.tiagocosmai.people.web;

import com.tiagocosmai.people.dto.CreateUserRequest;
import com.tiagocosmai.people.dto.MessageResponse;
import com.tiagocosmai.people.dto.UpdateUserRequest;
import com.tiagocosmai.people.dto.UserResponse;
import com.tiagocosmai.people.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Users")
@RestController
@RequestMapping("/api/user")
@SecurityRequirement(name = "JWT-auth")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @Operation(summary = "Create a new user (Admin only)")
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasAuthority('ADMIN')")
  public UserResponse create(@Valid @RequestBody CreateUserRequest body) {
    return userService.create(body);
  }

  @Operation(summary = "Get all users with optional search")
  @GetMapping
  @PreAuthorize("hasAnyAuthority('ADMIN','SYSTEM')")
  public List<UserResponse> findAll(@RequestParam(required = false) String search) {
    return userService.findAll(search);
  }

  @Operation(summary = "Get user by ID")
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('ADMIN','SYSTEM')")
  public UserResponse findOne(@PathVariable String id) {
    return userService.findOne(id);
  }

  @Operation(summary = "Update user (Admin only)")
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('ADMIN')")
  public UserResponse update(@PathVariable String id, @Valid @RequestBody UpdateUserRequest body) {
    return userService.update(id, body);
  }

  @Operation(summary = "Delete user (Admin only)")
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('ADMIN')")
  public MessageResponse remove(@PathVariable String id) {
    userService.remove(id);
    return new MessageResponse("User deleted successfully");
  }
}
