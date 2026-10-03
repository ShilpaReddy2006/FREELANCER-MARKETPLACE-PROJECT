package com.freelancemarketplace.project.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ProjectRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Budget is required")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "Budget must be greater than 0")
    private Double budget;

    @NotNull(message = "Deadline is required")
    private LocalDate deadline;

    public ProjectRequest() {
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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }
}