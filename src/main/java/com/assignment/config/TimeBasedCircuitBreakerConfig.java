package com.assignment.config;

import lombok.Getter;

@Getter
public class TimeBasedCircuitBreakerConfig extends BasicCircuitBreakerConfig {

    private final long thresholdTimeToOpenInMillis;

    public TimeBasedCircuitBreakerConfig(int configHalfOpenCallLimit, int configHalfOpenSuccessfulCallsToClose,
                                         long configOpenResetTimeoutMillis, long thresholdTimeToOpenInMillis) {
        super(configHalfOpenCallLimit, configHalfOpenSuccessfulCallsToClose, configOpenResetTimeoutMillis);
        this.thresholdTimeToOpenInMillis = thresholdTimeToOpenInMillis;
    }
}
