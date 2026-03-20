package com.tiagocosmai.people.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePersonRequest {

  @Size(min = 1)
  private String name;

  @Email
  private String email;

  private String phone;
  private String document;
  private String birthDate;
  private String address;
  private String city;
  private String state;
  private String zipCode;
  private String country;
}
