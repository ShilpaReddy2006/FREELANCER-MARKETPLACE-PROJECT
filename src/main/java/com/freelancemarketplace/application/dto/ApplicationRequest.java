package com.freelancemarketplace.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ApplicationRequest {

    @NotBlank(message = "Proposal is required")
    private String proposal;

    @NotNull(message = "Proposed budget is required")
    @DecimalMin(
            value = "0.0",
            inclusive = false,
            message = "Proposed budget must be greater than 0"
    )
    private Double proposedBudget;

    public ApplicationRequest() {
    }

    public String getProposal() {
        return proposal;
    }

    public Double getProposedBudget() {
        return proposedBudget;
    }

    public void setProposal(String proposal) {
        this.proposal = proposal;
    }

    public void setProposedBudget(Double proposedBudget) {
        this.proposedBudget = proposedBudget;
    }
}