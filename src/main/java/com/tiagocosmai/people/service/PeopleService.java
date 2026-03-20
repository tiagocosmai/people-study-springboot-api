package com.tiagocosmai.people.service;

import com.tiagocosmai.people.domain.Person;
import com.tiagocosmai.people.dto.CreatePersonRequest;
import com.tiagocosmai.people.dto.UpdatePersonRequest;
import com.tiagocosmai.people.repository.PersonRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PeopleService {

  private final PersonRepository personRepository;
  private final MongoTemplate mongoTemplate;

  public Person create(CreatePersonRequest req) {
    Person p =
        Person.builder()
            .name(req.getName())
            .email(req.getEmail())
            .phone(req.getPhone())
            .document(req.getDocument())
            .birthDate(parseBirthDate(req.getBirthDate()))
            .address(req.getAddress())
            .city(req.getCity())
            .state(req.getState())
            .zipCode(req.getZipCode())
            .country(req.getCountry())
            .build();
    return personRepository.save(p);
  }

  public List<Person> findAll(String search) {
    Query query = new Query().with(Sort.by(Sort.Direction.DESC, "createdAt"));
    if (search != null && !search.isBlank()) {
      String term = search.trim();
      List<Criteria> ors = new ArrayList<>();
      for (String field :
          List.of("name", "email", "phone", "document", "address", "city", "state", "zipCode", "country")) {
        ors.add(Criteria.where(field).regex(Pattern.quote(term), "i"));
      }
      query.addCriteria(new Criteria().orOperator(ors.toArray(new Criteria[0])));
    }
    return mongoTemplate.find(query, Person.class);
  }

  public Person findOne(String id) {
    return personRepository
        .findById(id)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person with ID " + id + " not found"));
  }

  public Person update(String id, UpdatePersonRequest req) {
    Person person = findOne(id);
    if (req.getName() != null && !req.getName().isBlank()) {
      person.setName(req.getName());
    }
    if (req.getEmail() != null && !req.getEmail().isBlank()) {
      person.setEmail(req.getEmail());
    }
    if (req.getPhone() != null) {
      person.setPhone(req.getPhone());
    }
    if (req.getDocument() != null) {
      person.setDocument(req.getDocument());
    }
    if (req.getBirthDate() != null) {
      person.setBirthDate(parseBirthDate(req.getBirthDate()));
    }
    if (req.getAddress() != null) {
      person.setAddress(req.getAddress());
    }
    if (req.getCity() != null) {
      person.setCity(req.getCity());
    }
    if (req.getState() != null) {
      person.setState(req.getState());
    }
    if (req.getZipCode() != null) {
      person.setZipCode(req.getZipCode());
    }
    if (req.getCountry() != null) {
      person.setCountry(req.getCountry());
    }
    return personRepository.save(person);
  }

  public void remove(String id) {
    if (!personRepository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Person with ID " + id + " not found");
    }
    personRepository.deleteById(id);
  }

  private static Instant parseBirthDate(String s) {
    if (s == null || s.isBlank()) {
      return null;
    }
    if (s.contains("T")) {
      return Instant.parse(s);
    }
    return LocalDate.parse(s).atStartOfDay(ZoneOffset.UTC).toInstant();
  }
}
