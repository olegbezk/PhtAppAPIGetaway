package com.api.geteway.security;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    private static final String BEARER = "Bearer";
    private static final String EMPTY = "";
    private static final int FIRST_ELEMENT = 0;

    private final Environment environment;

    public AuthorizationHeaderFilter(Environment environment) {
        super(Config.class);
        this.environment = environment;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onResponse(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authorizationHeader = Objects.requireNonNull(
                    request.getHeaders().get(HttpHeaders.AUTHORIZATION)).get(FIRST_ELEMENT);
            String jwt = authorizationHeader.replace(BEARER, EMPTY);

            if (!isJwtValid(jwt)) {
                return onResponse(exchange, "Jwt token is not valid", HttpStatus.UNAUTHORIZED);
            }

            return chain.filter(exchange);
        });
    }

    private Mono<Void> onResponse(ServerWebExchange exchange, String message, HttpStatus statusCode) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(statusCode);
        return response.setComplete();
    }

    private boolean isJwtValid(String jwt) {

        String subject;

        try {
            subject = Jwts.parser().setSigningKey(environment.getProperty("token.secret"))
                    .parseClaimsJws(jwt)
                    .getBody()
                    .getSubject();
        } catch (Exception ex) {
            return false;
        }

        return subject != null && !subject.isEmpty();
    }

    public static class Config{
        // put properties here
    }
}
