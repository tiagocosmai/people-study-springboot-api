package com.tiagocosmai.people.domain;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "people")
public class Person {

  @Id private String id;
  private String name;
  private String email;
  private String phone;
  private String document;
  private Instant birthDate;
  private String address;
  private String city;
  private String state;
  private String zipCode;
  private String country;

  @CreatedDate private Instant createdAt;
  @LastModifiedDate private Instant updatedAt;
}
