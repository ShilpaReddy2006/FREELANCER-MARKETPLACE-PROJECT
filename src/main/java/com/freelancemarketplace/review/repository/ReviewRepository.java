package com.freelancemarketplace.review.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.freelancemarketplace.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByContractIdAndReviewerId(
        Long contractId,
        Long reviewerId
    );

    List<Review> findByReviewerIdOrderByCreatedAtDesc(
        Long reviewerId
    );

    List<Review> findByRevieweeIdOrderByCreatedAtDesc(
        Long revieweeId
    );
}