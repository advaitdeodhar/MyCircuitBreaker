package com.assignment.circuitBreaker;

import com.assignment.utils.ThrowingFunction;
import com.assignment.config.RequestBasedCircuitBreakerConfig;
import com.assignment.enums.CircuitBreakerState;
import com.assignment.listener.ICircuitBreakerEventListener;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class RequestBasedCircuitBreaker<T, U> extends CustomCircuitBreaker<T, U, RequestBasedCircuitBreakerConfig> {

    private static final Logger logger = Logger.getLogger(RequestBasedCircuitBreaker.class.getName());


    protected AtomicInteger closedFailedCallsCount;

    public RequestBasedCircuitBreaker(String name, RequestBasedCircuitBreakerConfig config,
                                      ICircuitBreakerEventListener eventListener, ExecutorService executor) {
        super(name, config, eventListener, executor);
        closedFailedCallsCount = new AtomicInteger(0);
    }

    @Override
    protected U executeOnClosed(ThrowingFunction<T, U> action, T input) throws Exception {

        logger.info("Execution in CLOSED state.");

        try {
            U retVal = performAction(action, input);
            resetClose();
            return retVal;
        } catch (Exception e) {
            failed.inc();
            logger.warning("Error in action !");
            if (closedFailedCallsCount.incrementAndGet() > config.getClosedAllowedFailures()) {
                changeState(CircuitBreakerState.CLOSE, CircuitBreakerState.OPEN);
            }
            throw e;
        }
    }


    @Override
    protected void resetClose() {
        closedFailedCallsCount.set(0);
    }

}
