package com.booking.booking.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.resilience")
public class ResilienceProperties {

    private float failureRateThreshold = 50f;
    private int slidingWindowSize = 10;
    private int minimumNumberOfCalls = 5;
    private int permittedCallsInHalfOpenState = 3;
    private long waitDurationInOpenStateMs = 30000;
    private long timeoutMs = 5000;
    private boolean cancelRunningFuture = true;
    private List<Class<? extends Throwable>> recordExceptions = List.of(RuntimeException.class);

    public float getFailureRateThreshold() {
        return failureRateThreshold;
    }

    public void setFailureRateThreshold(float failureRateThreshold) {
        this.failureRateThreshold = failureRateThreshold;
    }

    public int getSlidingWindowSize() {
        return slidingWindowSize;
    }

    public void setSlidingWindowSize(int slidingWindowSize) {
        this.slidingWindowSize = slidingWindowSize;
    }

    public int getMinimumNumberOfCalls() {
        return minimumNumberOfCalls;
    }

    public void setMinimumNumberOfCalls(int minimumNumberOfCalls) {
        this.minimumNumberOfCalls = minimumNumberOfCalls;
    }

    public int getPermittedCallsInHalfOpenState() {
        return permittedCallsInHalfOpenState;
    }

    public void setPermittedCallsInHalfOpenState(int permittedCallsInHalfOpenState) {
        this.permittedCallsInHalfOpenState = permittedCallsInHalfOpenState;
    }

    public long getWaitDurationInOpenStateMs() {
        return waitDurationInOpenStateMs;
    }

    public void setWaitDurationInOpenStateMs(long waitDurationInOpenStateMs) {
        this.waitDurationInOpenStateMs = waitDurationInOpenStateMs;
    }

    public long getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(long timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public boolean isCancelRunningFuture() {
        return cancelRunningFuture;
    }

    public void setCancelRunningFuture(boolean cancelRunningFuture) {
        this.cancelRunningFuture = cancelRunningFuture;
    }

    public List<Class<? extends Throwable>> getRecordExceptions() {
        return recordExceptions;
    }

    public void setRecordExceptions(List<Class<? extends Throwable>> recordExceptions) {
        this.recordExceptions = recordExceptions;
    }

    public Class<? extends Throwable>[] resolveRecordableExceptions() {
        return recordExceptions.toArray(new Class[0]);
    }
}

