package com.example.aop.authz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Demo 05: Kiem tra quyen bang annotation @RequiresRole thay vi if/else trong tung ham.
 * LUU Y: header X-User/X-Roles chi de DEMO co che AOP. Thuc te dung Spring Security (@PreAuthorize
 * cung chinh la AOP: AuthorizationManagerBeforeMethodInterceptor) va lay danh tinh tu JWT da xac thuc.
 */
@SpringBootApplication
public class AuthorizationApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthorizationApplication.class, args);
    }
}
