package com.booking.shared.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenService {

    private final JwtProperties jwtProperties;

    public JwtTokenService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String generateToken(String username, Collection<? extends GrantedAuthority> authorities) {
        var now = java.time.Instant.now();
        var expiry = now.plusSeconds(jwtProperties.getValiditySeconds());

        return Jwts.builder()
            .subject(username)
            .claim(SecurityConstants.ROLES_CLAIM, authorities.stream().map(GrantedAuthority::getAuthority).toList())
            .claim(SecurityConstants.TOKEN_ID_CLAIM, UUID.randomUUID().toString())
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiry))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    public Authentication parseAuthentication(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();

        String username = claims.getSubject();
        Collection<String> roles = claims.get(SecurityConstants.ROLES_CLAIM, Collection.class);
        var authorities = roles == null ? java.util.List.<GrantedAuthority>of() : roles.stream()
            .map(SimpleGrantedAuthority::new)
            .toList();

        return new UsernamePasswordAuthenticationToken(username, token, authorities);
    }

    public Map<String, Object> extractClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

