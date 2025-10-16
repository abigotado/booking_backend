package com.booking.gateway.security;

import com.booking.shared.security.JwtTokenService;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import reactor.core.publisher.Mono;

/**
 * Reactive authentication manager delegating JWT parsing to shared JwtTokenService.
 */
public class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtTokenService jwtTokenService;

    public JwtReactiveAuthenticationManager(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        if (authentication instanceof BearerTokenAuthenticationToken bearerTokenAuthenticationToken) {
            String token = bearerTokenAuthenticationToken.getToken();
            return Mono.fromCallable(() -> jwtTokenService.parseAuthentication(token))
                .onErrorResume(ex -> Mono.empty())
                .flatMap(Mono::justOrEmpty);
        }
        return Mono.empty();
    }
}
