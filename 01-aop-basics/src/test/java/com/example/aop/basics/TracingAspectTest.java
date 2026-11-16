package com.example.aop.basics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.aop.basics.aspect.AdviceTrace;
import com.example.aop.basics.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TracingAspectTest {

    @Autowired
    OrderService orderService;
    @Autowired
    AdviceTrace trace;

    @BeforeEach
    void reset() {
        trace.clear();
    }

    @Test
    void beanDuocInjectLaCglibProxy() {
        assertThat(AopUtils.isAopProxy(orderService)).isTrue();
        assertThat(AopUtils.isCglibProxy(orderService)).isTrue();   // Spring Boot mac dinh proxyTargetClass=true
    }

    @Test
    void thuTuAdviceKhiThanhCong() {
        orderService.placeOrder("BOOK-01", 2);

        assertThat(trace.events()).containsExactly(
                "around-before", "before", "afterReturning:ORDER-BOOK-01-x2", "after", "around-after");
    }

    @Test
    void thuTuAdviceKhiNemLoi() {
        assertThatThrownBy(() -> orderService.placeOrder("BOOK-01", 0))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(trace.events()).containsExactly(
                "around-before", "before", "afterThrowing:IllegalArgumentException", "after", "around-after");
    }

    @Test
    void methodKhongMatchPointcutThiKhongBiIntercept() {
        orderService.countOrders();
        assertThat(trace.events()).isEmpty();
    }
}
