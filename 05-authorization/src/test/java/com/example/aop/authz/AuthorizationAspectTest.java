package com.example.aop.authz;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AuthorizationAspectTest {

    @Autowired
    MockMvc mvc;

    @Test
    void tellerXemDuocNhungKhongDuyetDuoc() throws Exception {
        mvc.perform(get("/loans/L-1").header("X-User", "teller01").header("X-Roles", "TELLER"))
                .andExpect(status().isOk());
        mvc.perform(post("/loans/L-1/approve").header("X-User", "teller01").header("X-Roles", "TELLER"))
                .andExpect(status().isForbidden());
    }

    @Test
    void managerDuyetDuoc() throws Exception {
        mvc.perform(post("/loans/L-1/approve").header("X-User", "mgr01").header("X-Roles", "MANAGER"))
                .andExpect(status().isOk())
                .andExpect(content().string("LOAN L-1 APPROVED"));
    }

    @Test
    void annotationCapClassApDungChoMoiMethod() throws Exception {
        mvc.perform(post("/admin/users/u1/reset-password").header("X-User", "mgr01").header("X-Roles", "MANAGER"))
                .andExpect(status().isForbidden());
        mvc.perform(post("/admin/users/u1/reset-password").header("X-User", "root").header("X-Roles", "ADMIN"))
                .andExpect(status().isOk());
    }

    @Test
    void chuaDangNhapThi401_methodKhongAnnotationThiTuDo() throws Exception {
        mvc.perform(get("/loans/L-1")).andExpect(status().isUnauthorized());
        mvc.perform(get("/loans/rate")).andExpect(status().isOk());
    }
}
