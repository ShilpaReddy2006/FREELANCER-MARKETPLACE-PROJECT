package com.freelancemarketplace.project.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.freelancemarketplace.project.entity.ProjectStatus;

public class ProjectResponse {

    private Long id;
    private Long clientId;
    private String title;
    private String description;
    private Double budget;
    private LocalDate deadline;
    private ProjectStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProjectResponse(
            Long id,
            Long clientId,
            String title,
            String description,
            Double budget,
            LocalDate deadline,
            ProjectStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.id = id;
        this.clientId = clientId;
        this.title = title;
        this.description = description;
        this.budget = budget;
        this.deadline = deadline;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getClientId() {
        return clientId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Double getBudget() {
        return budget;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}