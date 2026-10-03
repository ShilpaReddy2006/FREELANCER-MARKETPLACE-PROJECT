package com.freelancemarketplace.application.entity;

import java.time.LocalDateTime;

import com.freelancemarketplace.project.entity.Project;
import com.freelancemarketplace.user.entity.User;

import jakarta.persistence.*;

@Entity
@Table(
    name = "applications",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {"project_id", "freelancer_id"}
        )
    }
)
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "freelancer_id", nullable = false)
    private User freelancer;

    @Column(nullable = false, length = 2000)
    private String proposal;

    @Column(nullable = false)
    private Double proposedBudget;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Application() {
    }

    public Long getId() {
        return id;
    }

    public Project getProject() {
        return project;
    }

    public User getFreelancer() {
        return freelancer;
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setFreelancer(User freelancer) {
        this.freelancer = freelancer;
    }

    public void setProposal(String proposal) {
        this.proposal = proposal;
    }

    public void setProposedBudget(Double proposedBudget) {
        this.proposedBudget = proposedBudget;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}