package com.example.aop.authz.web;

import com.example.aop.authz.context.CurrentUser;
import com.example.aop.authz.context.UserContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** CHI DE DEMO: tin header tu client la khong an toan. Thuc te: xac thuc JWT/OAuth2 bang Spring Security. */
@Component
public class DemoAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String user = request.getHeader("X-User");
        if (user != null && !user.isBlank()) {
            String rolesHeader = request.getHeader("X-Roles");
            Set<String> roles = rolesHeader == null ? Set.of()
                    : Arrays.stream(rolesHeader.split(",")).map(String::trim).filter(s -> !s.isEmpty())
                    .collect(Collectors.toUnmodifiableSet());
            UserContext.set(new CurrentUser(user, roles));
        }
        try {
            chain.doFilter(request, response);
        } finally {
            UserContext.clear();
        }
    }
}
