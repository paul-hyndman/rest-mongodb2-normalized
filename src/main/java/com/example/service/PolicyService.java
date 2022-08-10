package com.example.service;

import com.example.domain.Insured;
import com.example.domain.Policy;
import com.example.persistence.PolicyLookup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Service to create policies, enforcing rule that an Insured must be known for a policy.
 */
@Component("policyservice")
public class PolicyService {
    @Autowired
    PolicyLookup mongoDbPolicyLookup;
    @Autowired
    KnowYourCustomer kyc;

    public Policy createPolicy(Policy policy) {
        // Use KnowYourCustomer service to return a known customer
        Insured policyInsured = policy.getInsured();
        policy.setInsured(kyc.getKnownInsured(policyInsured));
        // Create policy using known insured
        return mongoDbPolicyLookup.createPolicy(policy);
    }
}
