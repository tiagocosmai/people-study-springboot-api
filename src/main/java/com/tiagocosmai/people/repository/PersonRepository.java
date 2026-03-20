package com.tiagocosmai.people.repository;

import com.tiagocosmai.people.domain.Person;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PersonRepository extends MongoRepository<Person, String> {}
