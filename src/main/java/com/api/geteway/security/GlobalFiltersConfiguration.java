package com.api.geteway.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import reactor.core.publisher.Mono;

@Configuration
public class GlobalFiltersConfiguration {

    private final Logger log = LoggerFactory.getLogger(GlobalFiltersConfiguration.class);

    @Order(1)
    @Bean
    public GlobalFilter secondPreFilter() {
        return ((exchange, chain) -> {
            log.info("My second pre-filter was executed...");
            return chain.filter(exchange)
                    .then(Mono.fromRunnable(() -> {
                        log.info("My third post-filter was executed...");
                    }));
        });
    }

    @Order(2)
    @Bean
    public GlobalFilter thirdPreFilter() {
        return ((exchange, chain) -> {
            log.info("My third pre-filter was executed...");
            return chain.filter(exchange)
                    .then(Mono.fromRunnable(() -> {
                        log.info("My second post-filter was executed...");
                    }));
        });
    }

    @Order(3)
    @Bean
    public GlobalFilter fourthPreFilter() {
        return ((exchange, chain) -> {
            log.info("My fourth pre-filter was executed...");
            return chain.filter(exchange)
                    .then(Mono.fromRunnable(() -> {
                        log.info("My first post-filter was executed...");
                    }));
        });
    }
}
