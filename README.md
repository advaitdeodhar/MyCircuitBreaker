# Lucidity ( Custom Circuit Breaker Implementation )



## Background

- This is a stand-alone java library is built as a coding assignment for Interview round at Lucidity
- Circuit Breaker is resilience pattern that stops invoking failing service temporarily to avoid repetitive failures in micro-services 

## Insights 

- It is developed with IntelliJ IDEA, and It uses maven and Java 17 to build.
- This package includes
  - 2 Circuit Breaker Implementations
    - Time Based    : Responses in last N seconds failed or were slow
    - Request Based : Last N number of calls failed or were slow
  - Sample Services that are integrated with these Circuit Breaker Implementations
    - It can be done very quickly as Circuit Breaker works with any type on Input and Output classes.
  - Provision to make fine-tuned configurations
    - Granular control is given on Circuit Breaker Params, using this standard configs can be created.
  - Support for fallback mechanism when Circuit Breaker is in Open State
    - A pluggable fallback implementation is provided. 
  - Provision to act on state change events through event listener support
    - Custom event listeners can be integrated through event listener interface.
  - Support for key metrics related to circuit breaker
    - Metrics are published at the end of the test execution.
  - Driver Programs in driver package to test these implementations
    - These 2 Driver programs have their own main function to test corresponding Circuit Breaker.



