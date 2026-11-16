package com.example.aop.authz.context;

import java.util.Set;

public record CurrentUser(String username, Set<String> roles) {
}
