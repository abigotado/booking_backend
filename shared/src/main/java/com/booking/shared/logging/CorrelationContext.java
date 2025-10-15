package com.booking.shared.logging;

import java.util.UUID;

public final class CorrelationContext {

    private static final ThreadLocal<String> CORRELATION_ID = new ThreadLocal<>();

    private CorrelationContext() {
    }

    public static String getCorrelationId() {
        String id = CORRELATION_ID.get();
        if (id == null) {
            id = UUID.randomUUID().toString();
            CORRELATION_ID.set(id);
        }
        return id;
    }

    public static void setCorrelationId(String correlationId) {
        CORRELATION_ID.set(correlationId);
    }

    public static void clear() {
        CORRELATION_ID.remove();
    }
}

