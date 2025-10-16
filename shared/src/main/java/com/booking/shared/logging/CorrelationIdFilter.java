package com.booking.shared.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import org.springframework.stereotype.Component;

@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(CorrelationIdFilter.class);
    public static final String HEADER_CORRELATION_ID = "X-Correlation-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        try {
            String correlationId = resolveCorrelationId(request);
            CorrelationContext.setCorrelationId(correlationId);
            response.setHeader(HEADER_CORRELATION_ID, correlationId);
            filterChain.doFilter(request, response);
        } finally {
            CorrelationContext.clear();
        }
    }

    private String resolveCorrelationId(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(HEADER_CORRELATION_ID))
            .filter(id -> !id.isBlank())
            .orElseGet(() -> {
                String generatedId = CorrelationContext.getCorrelationId();
                log.debug("Generated correlation id: {}", generatedId);
                return generatedId;
            });
    }
}

