package com.freelancemarketplace.contract.dto;

import com.freelancemarketplace.contract.entity.ContractStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ContractResponse {

    private Long id;
    private Long projectId;
    private Long applicationId;
    private Long clientId;
    private Long freelancerId;
    private Double agreedAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private ContractStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ContractResponse(
            Long id,
            Long projectId,
            Long applicationId,
            Long clientId,
            Long freelancerId,
            Double agreedAmount,
            LocalDate startDate,
            LocalDate endDate,
            ContractStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.id = id;
        this.projectId = projectId;
        this.applicationId = applicationId;
        this.clientId = clientId;
        this.freelancerId = freelancerId;
        this.agreedAmount = agreedAmount;
        this.startDate = startDate;
        this.endDate = endDate;
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

    public Long getApplicationId() {
        return applicationId;
    }

    public Long getClientId() {
        return clientId;
    }

    public Long getFreelancerId() {
        return freelancerId;
    }

    public Double getAgreedAmount() {
        return agreedAmount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public ContractStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}