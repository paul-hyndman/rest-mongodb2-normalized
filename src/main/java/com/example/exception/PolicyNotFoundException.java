package com.example.exception;

public class PolicyNotFoundException extends RuntimeException {
    public PolicyNotFoundException(String policy) {
        super("Count not find policy " + policy);
    }
}
