package com.freelancemarketplace.application.dto;

import java.time.LocalDateTime;

import com.freelancemarketplace.application.entity.ApplicationStatus;

public class ApplicationResponse {

    private Long id;
    private Long projectId;
    private Long freelancerId;
    private String proposal;
    private Double proposedBudget;
    private ApplicationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ApplicationResponse(
            Long id,
            Long projectId,
            Long freelancerId,
            String proposal,
            Double proposedBudget,
            ApplicationStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.id = id;
        this.projectId = projectId;
        this.freelancerId = freelancerId;
        this.proposal = proposal;
        this.proposedBudget = proposedBudget;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Long getFreelancerId() {
        return freelancerId;
    }

    public String getProposal() {
        return proposal;
    }

    public Double getProposedBudget() {
        return proposedBudget;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}