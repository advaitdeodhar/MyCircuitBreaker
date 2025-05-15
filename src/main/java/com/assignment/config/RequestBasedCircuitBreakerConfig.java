package com.assignment.config;

import lombok.Getter;

@Getter
public class RequestBasedCircuitBreakerConfig extends BasicCircuitBreakerConfig {

    private final int closedAllowedFailures;

    public RequestBasedCircuitBreakerConfig(int halfOpenCallLimit, int halfOpenSuccessfulCallsToClose,
                                            long openResetTimeoutMillis, int closedAllowedFailures) {
        super(halfOpenCallLimit, halfOpenSuccessfulCallsToClose, openResetTimeoutMillis);
        this.closedAllowedFailures = closedAllowedFailures;
    }
}
