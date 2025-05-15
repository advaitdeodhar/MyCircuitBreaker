package com.assignment.utils;

public interface ThrowingFunction<T, U> {
    U apply(T t) throws Exception;
}
