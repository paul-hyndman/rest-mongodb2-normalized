package com.example.restservice;

import com.example.exception.DuplicatePolicyException;
import com.example.exception.PolicyNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class PolicyAdvice {
    @ResponseBody
    @ExceptionHandler(PolicyNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String employeeNotFoundHandler(PolicyNotFoundException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(DuplicatePolicyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String duplicatePolicyHandler(DuplicatePolicyException ex) {
        return ex.getMessage();
    }
}
