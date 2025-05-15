package com.assignment.service;

import com.assignment.circuitBreaker.TimeBasedCustomCircuitBreaker;
import com.assignment.config.TimeBasedCircuitBreakerConfig;
import com.assignment.listener.CircuitBreakerSimpleEventListener;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServiceWithTimeBasedCircuitBreaker {
    private final Set<Integer> errorValues;
    private final TimeBasedCustomCircuitBreaker<Integer, Integer> cb;
    private final ExecutorService executor;

    public ServiceWithTimeBasedCircuitBreaker(Set<Integer> errorValues) {

        executor = Executors.newSingleThreadExecutor();

        // This circuit breaker opens after failures for 3 seconds, and in half open state tries 4 requests and if 2 of them passes then closes again or opens the circuit breaker
        TimeBasedCircuitBreakerConfig config = new TimeBasedCircuitBreakerConfig(4, 2, 1000L, 3000L);

        this.cb = new TimeBasedCustomCircuitBreaker<Integer, Integer>("Time-Based-CB", config, new CircuitBreakerSimpleEventListener(), executor);
        this.errorValues = errorValues;
    }


    public Integer getSquare(Integer input) throws Exception {

        return cb.execute(this::getSquareInternal, input, this::getDefaultValue);
    }

    private Integer getSquareInternal(Integer input) throws Exception {

        if (errorValues.contains(input)) {
            throw new Exception("Input found in error values !");
        }

        return input * input;
    }

    public Integer getDefaultValue() {
        return -1;
    }

    public void shutDown() {
        cb.reportMetrics();
        executor.shutdown();
    }
}
