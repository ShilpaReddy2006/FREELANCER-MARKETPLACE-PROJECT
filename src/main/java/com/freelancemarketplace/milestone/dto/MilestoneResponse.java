
package com.freelancemarketplace.milestone.dto;

import com.freelancemarketplace.milestone.entity.MilestoneStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MilestoneResponse {

    private Long id;
    private Long contractId;
    private String title;
    private String description;
    private Double amount;
    private LocalDate dueDate;
    private MilestoneStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MilestoneResponse(
            Long id,
            Long contractId,
            String title,
            String description,
            Double amount,
            LocalDate dueDate,
            MilestoneStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.id = id;
        this.contractId = contractId;
        this.title = title;
        this.description = description;
        this.amount = amount;
        this.dueDate = dueDate;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public Long getContractId() { return contractId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Double getAmount() { return amount; }
    public LocalDate getDueDate() { return dueDate; }
    public MilestoneStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}