package com.booking.shared.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security.jwt")
public class JwtProperties {

    /**
     * Secret key used for signing JWT tokens.
     */
    private String secret;

    /**
     * Token validity in seconds.
     */
    private long validitySeconds;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getValiditySeconds() {
        return validitySeconds;
    }

    public void setValiditySeconds(long validitySeconds) {
        this.validitySeconds = validitySeconds;
    }
}

