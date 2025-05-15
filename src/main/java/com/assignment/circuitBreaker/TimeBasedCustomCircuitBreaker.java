package com.assignment.circuitBreaker;

import com.assignment.utils.ThrowingFunction;
import com.assignment.config.TimeBasedCircuitBreakerConfig;
import com.assignment.enums.CircuitBreakerState;
import com.assignment.listener.ICircuitBreakerEventListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public class TimeBasedCustomCircuitBreaker<T, U> extends CustomCircuitBreaker<T, U, TimeBasedCircuitBreakerConfig> {

    private static final Logger logger = Logger.getLogger(TimeBasedCustomCircuitBreaker.class.getName());


    protected AtomicLong lastFailedRequest;
    protected AtomicLong lastSuccessfulRequest;

    public TimeBasedCustomCircuitBreaker(String name, TimeBasedCircuitBreakerConfig config,
                                         ICircuitBreakerEventListener eventListener, ExecutorService executor) {
        super(name, config, eventListener, executor);
        long currentTime = System.currentTimeMillis();
        lastFailedRequest = new AtomicLong(currentTime);
        lastSuccessfulRequest = new AtomicLong(currentTime);

    }

    @Override
    protected U executeOnClosed(ThrowingFunction<T, U> action, T input) throws Exception {

        logger.info("Execution in CLOSED state.");

        try {
            U retVal = performAction(action, input);
            lastSuccessfulRequest.set(System.currentTimeMillis());
            return retVal;
        } catch (Exception e) {
            failed.inc();
            long currentTime = System.currentTimeMillis();
            if ((currentTime - lastSuccessfulRequest.get()) > config.getThresholdTimeToOpenInMillis() &&
                    lastSuccessfulRequest.get() < lastFailedRequest.get()) {
                changeState(CircuitBreakerState.CLOSE, CircuitBreakerState.OPEN);
            }
            lastFailedRequest.set(currentTime);
            throw e;
        }
    }

    @Override
    protected void resetClose() {
        long currentTime = System.currentTimeMillis();
        lastFailedRequest.set(currentTime);
        lastSuccessfulRequest.set(currentTime);
    }
}
