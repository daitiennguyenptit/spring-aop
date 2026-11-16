package com.example.aop.retry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.aop.retry.service.BusinessException;
import com.example.aop.retry.service.PaymentGatewayClient;
import com.example.aop.retry.service.TransientException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class RetryAspectTest {

    @Autowired
    PaymentGatewayClient client;

    @AfterEach
    void restore() {
        client.setFailuresBeforeSuccess(2);
    }

    @Test
    void loiTamThoiDuocRetryDenKhiThanhCong() {
        assertThat(client.charge("PAY-1")).isEqualTo("CHARGED PAY-1 after 3 attempt(s)");
        assertThat(client.attemptsOf("PAY-1")).isEqualTo(3);
    }

    @Test
    void hetSoLanThuThiNemLoiGoc() {
        client.setFailuresBeforeSuccess(10);
        assertThatThrownBy(() -> client.charge("PAY-2")).isInstanceOf(TransientException.class);
        assertThat(client.attemptsOf("PAY-2")).isEqualTo(3);
    }

    @Test
    void loiNghiepVuKhongRetry() {
        assertThatThrownBy(() -> client.charge("BLOCKED-1")).isInstanceOf(BusinessException.class);
        assertThat(client.attemptsOf("BLOCKED-1")).isEqualTo(1);
    }
}
