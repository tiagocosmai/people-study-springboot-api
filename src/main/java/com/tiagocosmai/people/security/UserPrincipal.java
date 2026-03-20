package com.tiagocosmai.people.security;

import com.tiagocosmai.people.domain.Role;
import java.io.Serializable;

public record UserPrincipal(String id, String username, Role role) implements Serializable {}
