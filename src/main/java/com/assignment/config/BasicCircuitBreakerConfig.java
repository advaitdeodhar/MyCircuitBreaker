package com.assignment.config;

import lombok.Data;

@Data
public abstract class BasicCircuitBreakerConfig {

    private final int halfOpenCallLimit;
    private final int halfOpenSuccessfulCallsToClose;
    private final long openResetTimeoutMillis;

    protected BasicCircuitBreakerConfig(int halfOpenCallLimit, int halfOpenSuccessfulCallsToClose, long openResetTimeoutMillis) {
        this.halfOpenCallLimit = halfOpenCallLimit;
        this.halfOpenSuccessfulCallsToClose = halfOpenSuccessfulCallsToClose;
        this.openResetTimeoutMillis = openResetTimeoutMillis;
    }
}
