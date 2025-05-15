package com.assignment.service;

import com.assignment.circuitBreaker.CustomCircuitBreaker;
import com.assignment.circuitBreaker.RequestBasedCircuitBreaker;
import com.assignment.config.RequestBasedCircuitBreakerConfig;
import com.assignment.listener.CircuitBreakerSimpleEventListener;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServiceWithRequestBasedCircuitBreaker {

    private final Set<Integer> errorValues;
    private final CustomCircuitBreaker<Integer, Integer, RequestBasedCircuitBreakerConfig> cb;
    private final ExecutorService executor;

    public ServiceWithRequestBasedCircuitBreaker(Set<Integer> errorValues) {

        executor = Executors.newSingleThreadExecutor();

        // This circuit breaker opens after 2 failures, and in half open state tries 4 requests and if 2 of them passes then closes again or opens the circuit breaker
        RequestBasedCircuitBreakerConfig config = new RequestBasedCircuitBreakerConfig(4, 2, 5000L, 2);

        this.cb = new RequestBasedCircuitBreaker<>("FirstCB", config, new CircuitBreakerSimpleEventListener(), executor);
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
