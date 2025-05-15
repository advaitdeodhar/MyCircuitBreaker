package com.assignment.listener;


import com.assignment.enums.CircuitBreakerState;

public interface ICircuitBreakerEventListener {

    void onStateChange(String name, CircuitBreakerState fromState, CircuitBreakerState toState);

}
