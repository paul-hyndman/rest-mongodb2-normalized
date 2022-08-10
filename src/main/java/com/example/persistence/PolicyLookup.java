package com.example.persistence;

import com.example.domain.Insured;
import com.example.domain.Policy;
import com.example.exception.InsuredNotFoundException;

public interface PolicyLookup {
    Policy getPolicy(String number);
    Policy createPolicy(Policy policy);
    Insured getInsured(String ssn) throws InsuredNotFoundException;
    Insured createInsured(Insured policyInsured);
}
