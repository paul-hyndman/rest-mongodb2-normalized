package com.example.domain;

/**
 * A POJO for an insurance policy.
 * Note there are no dependencies (imports) beyond core Java
 */
public class Policy {
    private String policyNumber;
    private int faceAmount;
    private Insured insured;

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public int getFaceAmount() {
        return faceAmount;
    }

    public void setFaceAmount(int faceAmount) {
        this.faceAmount = faceAmount;
    }

    public Insured getInsured() {
        return insured;
    }

    public void setInsured(Insured insured) {
        this.insured = insured;
    }
}
