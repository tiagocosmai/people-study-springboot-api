package com.tiagocosmai.people.web;

import com.tiagocosmai.people.domain.Person;
import com.tiagocosmai.people.dto.CreatePersonRequest;
import com.tiagocosmai.people.dto.MessageResponse;
import com.tiagocosmai.people.dto.UpdatePersonRequest;
import com.tiagocosmai.people.service.PeopleService;
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

@Tag(name = "People")
@RestController
@RequestMapping("/api/people")
@SecurityRequirement(name = "JWT-auth")
@RequiredArgsConstructor
public class PeopleController {

  private final PeopleService peopleService;

  @Operation(summary = "Create a new person")
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasAnyAuthority('ADMIN','SYSTEM')")
  public Person create(@Valid @RequestBody CreatePersonRequest body) {
    return peopleService.create(body);
  }

  @Operation(summary = "Get all people with optional search")
  @GetMapping
  @PreAuthorize("hasAnyAuthority('ADMIN','SYSTEM','VIEWER')")
  public List<Person> findAll(@RequestParam(required = false) String search) {
    return peopleService.findAll(search);
  }

  @Operation(summary = "Get person by ID")
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('ADMIN','SYSTEM','VIEWER')")
  public Person findOne(@PathVariable String id) {
    return peopleService.findOne(id);
  }

  @Operation(summary = "Update person")
  @PutMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('ADMIN','SYSTEM')")
  public Person update(@PathVariable String id, @Valid @RequestBody UpdatePersonRequest body) {
    return peopleService.update(id, body);
  }

  @Operation(summary = "Delete person")
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('ADMIN','SYSTEM')")
  public MessageResponse remove(@PathVariable String id) {
    peopleService.remove(id);
    return new MessageResponse("Person deleted successfully");
  }
}
