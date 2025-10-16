package com.booking.booking.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import java.time.Duration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ResilienceProperties.class)
public class ResilienceConfig {

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> resilienceCustomizer(ResilienceProperties properties) {
        return factory -> factory.configure(builder -> builder
            .timeLimiterConfig(timeLimiterConfig(properties))
            .circuitBreakerConfig(circuitBreakerConfig(properties)), "hotelService");
    }

    private TimeLimiterConfig timeLimiterConfig(ResilienceProperties properties) {
        return TimeLimiterConfig.custom()
            .timeoutDuration(Duration.ofMillis(properties.getTimeoutMs()))
            .cancelRunningFuture(properties.isCancelRunningFuture())
            .build();
    }

    private CircuitBreakerConfig circuitBreakerConfig(ResilienceProperties properties) {
        return CircuitBreakerConfig.custom()
            .failureRateThreshold(properties.getFailureRateThreshold())
            .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
            .slidingWindowSize(properties.getSlidingWindowSize())
            .minimumNumberOfCalls(properties.getMinimumNumberOfCalls())
            .waitDurationInOpenState(Duration.ofMillis(properties.getWaitDurationInOpenStateMs()))
            .permittedNumberOfCallsInHalfOpenState(properties.getPermittedCallsInHalfOpenState())
            .build();
    }
}
 
