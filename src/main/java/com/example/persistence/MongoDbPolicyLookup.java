package com.example.persistence;

import com.example.domain.Insured;
import com.example.domain.Policy;
import com.example.exception.DuplicatePolicyException;
import com.example.exception.InsuredNotFoundException;
import com.example.exception.PolicyNotFoundException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.mongodb.client.model.Aggregates.lookup;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;

@Component("mongoDbPolicyLookup")
public class MongoDbPolicyLookup implements PolicyLookup {
    @Value("${mongo.client}")
    private String mongoClient;
    @Value("${mongo.db}")
    private String mongoDb;

    public Policy getPolicy(String number) {
        // Use pattern of entities having platform-specific, but only exposing natural key
        // when entity supports a natural key
        Document doc = (Document) getPolicyCollection().find(eq("policyNumber", number)).first();
        if (doc == null) {
            throw new PolicyNotFoundException(number);
        }
        String json = doc.toJson();

        // Gson is used for mapping to keep Mongo-specific annotations out of Domain class
        Gson gson = new Gson();
        Policy policy = gson.fromJson(json, Policy.class);
        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
        String insuredId = jsonObject.getAsJsonPrimitive("insured_id").getAsString();

        // Find Insured by Object ID
        doc = (Document) getInsuredCollection().find(eq("_id", new ObjectId(insuredId))).first();
        json = doc.toJson();
        Insured insured = gson.fromJson(json, Insured.class);
        policy.setInsured(insured);

        return policy;
    }

    @Override
    public Insured getInsured(String ssn) throws InsuredNotFoundException {
        // Find Insured by SSN unique index
        Document doc = (Document) getInsuredCollection().find(eq("ssn", ssn)).first();
        if (doc == null) {
            throw new InsuredNotFoundException();
        }
        String json = doc.toJson();
        Insured insured = new Gson().fromJson(json, Insured.class);
        return insured;
    }

    @Override
    public Insured createInsured(Insured insured) {
        try {
            MongoCollection mongoCollection = getInsuredCollection();
            Document document = new Document()
                    .append("ssn", insured.getSsn())
                    .append("name", insured.getName())
                    .append("age", insured.getAge())
                    .append("streetAddress", insured.getStreetAddress())
                    .append("city", insured.getCity())
                    .append("state", insured.getState())
                    .append("zipCode", insured.getZipCode());
            mongoCollection.insertOne(document);
            return getInsured(insured.getSsn());
        } catch (InsuredNotFoundException ex) {
            // Unrecoverable error
            throw new RuntimeException(ex);
        }
    }

    public Policy createPolicy(Policy policy) {
        MongoCollection mongoCollection = getPolicyCollection();
        try {
            Document document = new Document()
                    .append("policyNumber", policy.getPolicyNumber())
                    .append("faceAmount", policy.getFaceAmount())
                    .append("insured_id", getInsuredId(policy.getInsured().getSsn()));
            mongoCollection.insertOne(document);
        } catch (Exception ex) {
            if (ex.getMessage().contains("E11000 duplicate key")) {
                throw new DuplicatePolicyException(policy.getPolicyNumber());
            }
        }
        return getPolicy(policy.getPolicyNumber());
    }

    private String getInsuredId(String ssn) {
        Document doc = (Document) getInsuredCollection().find(eq("ssn", ssn)).first();;
        JsonObject jsonObject = new JsonParser().parse(doc.toJson()).getAsJsonObject();
        String insuredId = jsonObject.getAsJsonObject("_id").getAsJsonPrimitive("$oid").getAsString();
        return insuredId;
    }

    private MongoCollection getPolicyCollection() {
        MongoClient client = MongoClients.create(mongoClient);
        MongoDatabase database = client.getDatabase(mongoDb);
        MongoCollection mongoCollection = database.getCollection("policies_normalized");
        return mongoCollection;
    }

    private MongoCollection getInsuredCollection() {
        MongoClient client = MongoClients.create(mongoClient);
        MongoDatabase database = client.getDatabase(mongoDb);
        MongoCollection mongoCollection = database.getCollection("insureds");
        return mongoCollection;
    }
}
