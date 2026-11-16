package com.example.aop.basics;

import com.example.aop.basics.aspect.AdviceTrace;
import com.example.aop.basics.service.OrderService;
import org.springframework.aop.support.AopUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Demo 01: 5 loai advice, pointcut expression va co che proxy.
 * Chay: mvn -pl 01-aop-basics spring-boot:run  -> xem log thu tu advice.
 */
@SpringBootApplication
public class BasicsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BasicsApplication.class, args);
    }

    @Bean
    CommandLineRunner demo(OrderService orderService, AdviceTrace trace) {
        return args -> {
            System.out.println("Bean class      : " + orderService.getClass().getName());
            System.out.println("La AOP proxy?   : " + AopUtils.isAopProxy(orderService));
            System.out.println("La CGLIB proxy? : " + AopUtils.isCglibProxy(orderService));

            orderService.placeOrder("BOOK-01", 2);
            System.out.println("Thu tu advice (thanh cong): " + trace.events());

            trace.clear();
            try {
                orderService.placeOrder("BOOK-01", 0);
            } catch (IllegalArgumentException ignored) {
                // demo
            }
            System.out.println("Thu tu advice (loi)       : " + trace.events());
        };
    }
}
