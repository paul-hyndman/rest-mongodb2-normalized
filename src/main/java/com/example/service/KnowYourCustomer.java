package com.example.service;

import com.example.domain.Insured;
import com.example.exception.InsuredNotFoundException;
import com.example.persistence.PolicyLookup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Class finds known and persisted Insured, and if not present then creates a known entity
 */
@Component("mongoDbInsuredLookup")
public class KnowYourCustomer {
    @Autowired
    PolicyLookup mongoDbPolicyLookup;

    public Insured getKnownInsured(Insured policyInsured) {
        try {
            return mongoDbPolicyLookup.getInsured(policyInsured.getSsn());
        } catch (InsuredNotFoundException ex) {
            return mongoDbPolicyLookup.createInsured(policyInsured);
        }
    }
}
