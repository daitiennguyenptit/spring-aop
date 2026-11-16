package com.example.aop.audit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.aop.audit.context.RequestContext;
import com.example.aop.audit.service.AccountService;
import com.example.aop.audit.store.AuditLogStore;
import com.example.aop.audit.store.AuditRecord;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AuditRequestIdTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    AuditLogStore store;
    @Autowired
    AccountService accountService;

    @BeforeEach
    void reset() {
        store.clear();
    }

    @Test
    void moiHamTrongCungRequestDeuCoCungRequestId_keCaAsync() throws Exception {
        mvc.perform(post("/api/transfers")
                        .header("X-Request-Id", "req-test-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"from":"ACC-001","to":"ACC-002","amount":10.00,"otp":"123456"}
                                """))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Request-Id", "req-test-001"));

        List<AuditRecord> records = awaitRecords("req-test-001", 4);

        assertThat(records).extracting(AuditRecord::action)
                .containsExactlyInAnyOrder("TRANSFER", "DEBIT", "CREDIT", "NOTIFY");
        assertThat(records).allMatch(r -> r.status().equals(AuditRecord.SUCCESS));

        AuditRecord notify = records.stream().filter(r -> r.action().equals("NOTIFY")).findFirst().orElseThrow();
        assertThat(notify.thread()).startsWith("async-");   // thread khac nhung van dung requestId

        AuditRecord transfer = records.stream().filter(r -> r.action().equals("TRANSFER")).findFirst().orElseThrow();
        assertThat(transfer.arguments()).contains("otp=***").doesNotContain("123456");
        assertThat(transfer.arguments()).contains("fromAccount=ACC-001");
    }

    @Test
    void khongGuiHeaderThiTuSinhRequestId() throws Exception {
        String generated = mvc.perform(get("/api/accounts/ACC-001/balance"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getHeader("X-Request-Id");

        assertThat(generated).isNotBlank();
        assertThat(store.findByRequestId(generated)).singleElement()
                .satisfies(r -> assertThat(r.action()).isEqualTo("VIEW_BALANCE"));
    }

    @Test
    void loiNghiepVuVanDuocAuditVaTraRequestIdChoClient() throws Exception {
        mvc.perform(post("/api/transfers")
                        .header("X-Request-Id", "req-fail-002")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"from":"ACC-002","to":"ACC-001","amount":999999,"otp":"000000"}
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.requestId").value("req-fail-002"));

        assertThat(store.findByRequestId("req-fail-002"))
                .extracting(AuditRecord::action, AuditRecord::status)
                .containsExactlyInAnyOrder(
                        org.assertj.core.groups.Tuple.tuple("DEBIT", AuditRecord.FAILURE),
                        org.assertj.core.groups.Tuple.tuple("TRANSFER", AuditRecord.FAILURE));
    }

    @Test
    void contextDuocDonDepSauRequest() throws Exception {
        mvc.perform(get("/api/accounts/ACC-001/balance").header("X-Request-Id", "req-clean"));
        // MockMvc chay filter tren chinh thread test -> sau request ThreadLocal phai rong
        assertThat(RequestContext.getRequestId()).isNull();
    }

    @Test
    void entryPointKhongPhaiHttp_vanGanDuocRequestId() {
        RequestContext.callWithRequestId("job-nightly-42", () -> accountService.getBalance("ACC-001"));

        assertThat(store.findByRequestId("job-nightly-42")).hasSize(1);
        assertThat(RequestContext.getRequestId()).isNull();
    }

    @Test
    void goiNgoaiMoiContextThiDanhDauNoRequestId() {
        accountService.getBalance("ACC-002");
        assertThat(store.findByRequestId("NO-REQUEST-ID")).hasSize(1);
    }

    private List<AuditRecord> awaitRecords(String requestId, int expected) throws InterruptedException {
        long deadline = System.currentTimeMillis() + 3000;
        List<AuditRecord> records = store.findByRequestId(requestId);
        while (records.size() < expected && System.currentTimeMillis() < deadline) {
            Thread.sleep(20);
            records = store.findByRequestId(requestId);
        }
        return records;
    }
}
