package com.example.aop.authz.service;

import com.example.aop.authz.annotation.RequiresRole;
import org.springframework.stereotype.Service;

/** Annotation o class -> ap dung cho moi method public. */
@Service
@RequiresRole("ADMIN")
public class AdminService {

    public String resetUserPassword(String username) {
        return "Password reset link sent to " + username;
    }
}
