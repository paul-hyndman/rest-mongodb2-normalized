package com.example.exception;

public class PolicyNotFoundException extends RuntimeException {
    public PolicyNotFoundException(String policy) {
        super("Policy " + policy + " not found");
    }
}
