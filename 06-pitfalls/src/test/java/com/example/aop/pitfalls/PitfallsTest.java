package com.example.aop.pitfalls;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.aop.pitfalls.aspect.TraceRecorder;
import com.example.aop.pitfalls.service.AopContextInvoiceService;
import com.example.aop.pitfalls.service.BuggyInvoiceService;
import com.example.aop.pitfalls.service.RefactoredInvoiceService;
import com.example.aop.pitfalls.service.SelfInjectedInvoiceService;
import com.example.aop.pitfalls.service.TaxCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PitfallsTest {

    @Autowired
    TraceRecorder recorder;
    @Autowired
    BuggyInvoiceService buggy;
    @Autowired
    SelfInjectedInvoiceService selfInjected;
    @Autowired
    AopContextInvoiceService aopContext;
    @Autowired
    RefactoredInvoiceService refactored;

    @BeforeEach
    void reset() {
        recorder.clear();
    }

    @Test
    void pitfall1_selfInvocationKhongQuaProxy() {
        buggy.issue(100);
        assertThat(recorder.calls()).containsExactly("BuggyInvoiceService.issue");   // thieu validate!
    }

    @Test
    void pitfall2_finalMethodKhongBiInterceptVaChayTrenProxy() {
        String result = buggy.finalMethod();
        assertThat(recorder.calls()).isEmpty();
        assertThat(result).contains("NULL");
    }

    @Test
    void pitfall3_objectTaoBangNewKhongPhaiBeanNenKhongCoAop() {
        new BuggyInvoiceService(new TaxCalculator()).issue(100);
        assertThat(recorder.calls()).isEmpty();
    }

    @Test
    void fix1_selfInjection() {
        selfInjected.issue(100);
        assertThat(recorder.calls()).containsExactly(
                "SelfInjectedInvoiceService.issue", "SelfInjectedInvoiceService.validate");
    }

    @Test
    void fix2_aopContextCurrentProxy() {
        aopContext.issue(100);
        assertThat(recorder.calls()).containsExactly(
                "AopContextInvoiceService.issue", "AopContextInvoiceService.validate");
    }

    @Test
    void fix3_tachBeanRieng() {
        refactored.issue(100);
        assertThat(recorder.calls()).containsExactly(
                "RefactoredInvoiceService.issue", "InvoiceValidator.validate");
    }
}
