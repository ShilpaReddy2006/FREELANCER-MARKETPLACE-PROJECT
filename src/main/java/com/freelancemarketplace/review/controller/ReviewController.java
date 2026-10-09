package com.freelancemarketplace.review.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.freelancemarketplace.review.dto.ReviewRequest;
import com.freelancemarketplace.review.dto.ReviewResponse;
import com.freelancemarketplace.review.service.ReviewService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // Submit a review for a completed contract.
    @PostMapping("/contracts/{contractId}")
    @PreAuthorize("hasAnyRole('CLIENT', 'FREELANCER')")
    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable Long contractId,
            @Valid @RequestBody ReviewRequest request,
            Authentication authentication) {

        Long reviewerId = (Long) authentication.getPrincipal();

        ReviewResponse response = reviewService.createReview(
            contractId, request, reviewerId
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // View reviews submitted by the logged-in user.
    @GetMapping("/my-given")
    @PreAuthorize("hasAnyRole('CLIENT', 'FREELANCER')")
    public ResponseEntity<List<ReviewResponse>> getReviewsGiven(
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();

        return ResponseEntity.ok(
            reviewService.getReviewsGiven(userId)
        );
    }

    // View reviews received by the logged-in user.
    @GetMapping("/my-received")
    @PreAuthorize("hasAnyRole('CLIENT', 'FREELANCER')")
    public ResponseEntity<List<ReviewResponse>> getReviewsReceived(
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();

        return ResponseEntity.ok(
            reviewService.getReviewsReceived(userId)
        );
    }
}