package com.assignment.circuitBreaker;

import com.assignment.utils.ThrowingFunction;
import com.assignment.config.BasicCircuitBreakerConfig;
import com.assignment.enums.CircuitBreakerState;
import com.assignment.listener.ICircuitBreakerEventListener;
import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;


import java.time.Instant;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.logging.Logger;

public abstract class CustomCircuitBreaker<T, U, C extends BasicCircuitBreakerConfig> {


    private static final Logger logger = Logger.getLogger(CustomCircuitBreaker.class.getName());


    protected final String name;
    protected final C config;
    protected final ICircuitBreakerEventListener eventListener;
    protected final ExecutorService executorService;


    protected AtomicReference<CircuitBreakerState> state = new AtomicReference<>(CircuitBreakerState.CLOSE);
    private final AtomicInteger halfOpenCallsCount;
    private final AtomicInteger halfOpenSuccessCallCount;
    MetricRegistry metrics;
    Counter requests;
    Counter fallbacks;
    Counter attempts;
    Counter failed;

    private Instant openTime;

    protected abstract U executeOnClosed(ThrowingFunction<T, U> action, T input) throws Exception;

    protected abstract void resetClose();


    public CustomCircuitBreaker(String name, C config, ICircuitBreakerEventListener eventListener, ExecutorService executor) {
        this.name = name;
        this.config = config;
        this.executorService = executor;
        this.eventListener = eventListener;
        halfOpenCallsCount = new AtomicInteger(0);
        halfOpenSuccessCallCount = new AtomicInteger(0);
        metrics = new MetricRegistry();
        requests = metrics.counter("requests");
        fallbacks = metrics.counter("fallbacks");
        attempts = metrics.counter("attempts");
        failed = metrics.counter("failed");

    }

    public U execute(ThrowingFunction<T, U> action, T input, Supplier<U> fallback) throws Exception {

        requests.inc();
        logger.info("Execution started !");
        return switch (state.get()) {
            case CLOSE -> executeOnClosed(action, input);
            case HALF_OPEN -> executeOnHalfOpen(action, input);
            case OPEN -> executeOnOpen(action, input, fallback);
        };
    }

    private U executeOnOpen(ThrowingFunction<T, U> action, T input, Supplier<U> fallback) throws Exception {

        logger.info("Execution in OPEN state.");

        Instant currentMillis = Instant.now();
        if (currentMillis.isAfter(openTime.plusMillis(config.getOpenResetTimeoutMillis()))) {
            changeState(CircuitBreakerState.OPEN, CircuitBreakerState.HALF_OPEN);
            return executeOnHalfOpen(action, input);
        } else {
            fallbacks.inc();
            return fallback.get();
        }
    }


    private U executeOnHalfOpen(ThrowingFunction<T, U> action, T input) throws Exception {

        logger.info("Execution in HALF_OPEN state.");

        try {
            halfOpenCallsCount.incrementAndGet();
            attempts.inc();
            U retVal = action.apply(input);
            halfOpenSuccessCallCount.incrementAndGet();
            return retVal;
        } catch (Exception e) {
            failed.inc();
            throw e;
        } finally {
            if (halfOpenCallsCount.get() >= config.getHalfOpenCallLimit()) {
                if (halfOpenSuccessCallCount.get() >= config.getHalfOpenSuccessfulCallsToClose()) {
                    changeState(CircuitBreakerState.HALF_OPEN, CircuitBreakerState.CLOSE);
                } else {
                    changeState(CircuitBreakerState.HALF_OPEN, CircuitBreakerState.OPEN);
                }
            }
        }
    }

    protected U performAction(ThrowingFunction<T, U> action, T input) throws Exception {

        Callable<U> task = () -> action.apply(input);

        Future<U> future = executorService.submit(task);

        try {
            attempts.inc();
            return future.get(1000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.warning("Error in action !");
            future.cancel(true);
            throw e;
        }

    }


    public void reportMetrics() {
        ConsoleReporter reporter = ConsoleReporter.forRegistry(metrics).build();
        reporter.report();

    }

    protected void changeState(CircuitBreakerState currentState, CircuitBreakerState nextState) {

        if (state.compareAndSet(currentState, nextState)) {
            logger.info(String.format("State change: %s -> %s", currentState, nextState));

            // Once the state is changed, all the new state related counters should be reset.
            switch (state.get()) {
                case OPEN -> resetOpen();
                case CLOSE -> resetClose();
                case HALF_OPEN -> resetHalfOpen();
            }

            eventListener.onStateChange(name, currentState, nextState);
        }
    }

    private void resetOpen() {
        openTime = Instant.now();
    }

    private void resetHalfOpen() {
        halfOpenCallsCount.set(0);
        halfOpenSuccessCallCount.set(0);
    }
}
