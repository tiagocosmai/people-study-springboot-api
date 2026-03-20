package com.tiagocosmai.people;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class PeopleApplication {

  public static void main(String[] args) {
    SpringApplication.run(PeopleApplication.class, args);
  }
}
