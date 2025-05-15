package com.assignment.listener;


import com.assignment.enums.CircuitBreakerState;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class CircuitBreakerSimpleEventListener implements ICircuitBreakerEventListener {
    private static final Logger logger = Logger.getLogger(CircuitBreakerSimpleEventListener.class.getName());

    @Override
    public void onStateChange(String name, @NotNull CircuitBreakerState fromState, @NotNull CircuitBreakerState toState) {
        logger.info(String.format("Circuit Breaker %s changed its state from %s to %s%n", name, fromState.name(), toState.name()));
    }
}
