package com.booking.shared.security;

public final class SecurityConstants {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String ROLES_CLAIM = "roles";
    public static final String TOKEN_ID_CLAIM = "jti";

    private SecurityConstants() {
        // utility class
    }
}

