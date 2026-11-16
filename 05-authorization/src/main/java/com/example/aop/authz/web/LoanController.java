package com.example.aop.authz.web;

import com.example.aop.authz.aspect.AccessDeniedException;
import com.example.aop.authz.aspect.UnauthenticatedException;
import com.example.aop.authz.service.AdminService;
import com.example.aop.authz.service.LoanService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoanController {

    private final LoanService loanService;
    private final AdminService adminService;

    public LoanController(LoanService loanService, AdminService adminService) {
        this.loanService = loanService;
        this.adminService = adminService;
    }

    @GetMapping("/loans/{id}")
    public String view(@PathVariable String id) {
        return loanService.viewLoan(id);
    }

    @PostMapping("/loans/{id}/approve")
    public String approve(@PathVariable String id) {
        return loanService.approveLoan(id);
    }

    @GetMapping("/loans/rate")
    public String rate() {
        return loanService.interestRate();
    }

    @PostMapping("/admin/users/{username}/reset-password")
    public String reset(@PathVariable String username) {
        return adminService.resetUserPassword(username);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String forbidden() {
        return "FORBIDDEN";
    }

    @ExceptionHandler(UnauthenticatedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String unauthorized() {
        return "UNAUTHORIZED";
    }
}
