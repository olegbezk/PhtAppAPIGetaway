package com.api.geteway.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Set;


@Component
public class MyPreFilter implements GlobalFilter, Ordered {

    private final Logger log = LoggerFactory.getLogger(MyPreFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        log.info("My first Pre-Filter was executed...");

        String path = exchange.getRequest().getPath().toString();
        log.info("Request path: " + path);

        HttpHeaders headers = exchange.getRequest().getHeaders();
        headers.keySet()
                .forEach(headerName -> {
                    log.info(headerName + " " + headers.getFirst(headerName));
                });

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
