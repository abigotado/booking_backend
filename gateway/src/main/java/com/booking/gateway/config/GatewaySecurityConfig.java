package com.booking.gateway.config;

import com.booking.gateway.security.JwtReactiveAuthenticationManager;
import com.booking.gateway.security.JwtServerAuthenticationConverter;
import com.booking.shared.security.JwtTokenService;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableReactiveMethodSecurity
@ImportAutoConfiguration(exclude = SecurityAutoConfiguration.class)
@EnableConfigurationProperties(com.booking.shared.security.JwtProperties.class)
public class GatewaySecurityConfig {

    private final JwtTokenService jwtTokenService;

    public GatewaySecurityConfig(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Bean
    public SecurityWebFilterChain gatewaySecurityFilterChain(ServerHttpSecurity http) {
        ReactiveAuthenticationManager authenticationManager = new JwtReactiveAuthenticationManager(jwtTokenService);

        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            .oauth2ResourceServer(resourceServer -> resourceServer
                .authenticationManagerResolver(context -> Mono.just(authenticationManager))
                .bearerTokenConverter(new JwtServerAuthenticationConverter()))
            .authorizeExchange(exchange -> exchange
                .pathMatchers(HttpMethod.GET, "/actuator/**").permitAll()
                .anyExchange().authenticated())
            .build();
    }
}
