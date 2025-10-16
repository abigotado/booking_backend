package com.booking.shared.logging;

import java.util.UUID;

public final class CorrelationContext {

    public static final String MDC_KEY = "correlationId";

    private static final ThreadLocal<String> CORRELATION_ID = ThreadLocal.withInitial(() -> UUID.randomUUID().toString());

    private CorrelationContext() {
    }

    public static String getCorrelationId() {
        return CORRELATION_ID.get();
    }

    public static void setCorrelationId(String correlationId) {
        CORRELATION_ID.set(correlationId);
    }

    public static void clear() {
        CORRELATION_ID.remove();
    }
}
 
