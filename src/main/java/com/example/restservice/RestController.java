package com.example.restservice;

import com.example.domain.Insured;
import com.example.domain.Policy;
import com.example.persistence.MongoDbPolicyLookup;
import com.example.persistence.PolicyLookup;
import com.example.service.KnowYourCustomer;
import com.example.service.PolicyService;
import com.mongodb.client.*;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Iterator;

import static com.mongodb.client.model.Filters.eq;

@org.springframework.web.bind.annotation.RestController
@Component
public class RestController {

    @Autowired
    PolicyLookup mongoDbPolicyLookup;
    @Autowired
    PolicyService policyService;

    @GetMapping(
            value = "/policy/{number}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getPolicy(@PathVariable String number) {
        Policy policy = mongoDbPolicyLookup.getPolicy(number);
        return new ResponseEntity<>(policy, HttpStatus.OK);
    }

    @PostMapping(
            value = "/policy",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE
            })
    public ResponseEntity<Policy> addPolicy(@RequestBody Policy policy) {
        Policy newPolicy = policyService.createPolicy(policy);
        // Return resource URL in header
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{number}")
                .buildAndExpand(newPolicy.getPolicyNumber())
                .toUri();
        return ResponseEntity.created(location).build();
    }
}
