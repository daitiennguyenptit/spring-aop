package com.example.aop.authz.service;

import com.example.aop.authz.annotation.RequiresRole;
import org.springframework.stereotype.Service;

@Service
public class LoanService {

    @RequiresRole({"TELLER", "MANAGER"})
    public String viewLoan(String loanId) {
        return "LOAN " + loanId + " status=PENDING";
    }

    @RequiresRole("MANAGER")
    public String approveLoan(String loanId) {
        return "LOAN " + loanId + " APPROVED";
    }

    /** Khong annotation -> ai cung goi duoc. */
    public String interestRate() {
        return "8.5%";
    }
}
