package com.freelancemarketplace.review.dto;

import java.time.LocalDateTime;

public class ReviewResponse {

    private Long id;
    private Long contractId;
    private Long projectId;
    private Long reviewerId;
    private Long revieweeId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;

    public ReviewResponse(
            Long id,
            Long contractId,
            Long projectId,
            Long reviewerId,
            Long revieweeId,
            Integer rating,
            String comment,
            LocalDateTime createdAt) {

        this.id = id;
        this.contractId = contractId;
        this.projectId = projectId;
        this.reviewerId = reviewerId;
        this.revieweeId = revieweeId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getContractId() {
        return contractId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Long getReviewerId() {
        return reviewerId;
    }

    public Long getRevieweeId() {
        return revieweeId;
    }

    public Integer getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}