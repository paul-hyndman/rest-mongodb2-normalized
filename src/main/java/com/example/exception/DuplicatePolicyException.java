package com.example.exception;

public class DuplicatePolicyException extends RuntimeException {

    public DuplicatePolicyException(String policy) {
        super("Policy number " + policy + " already exists");
    }
}
