package com.freelancemarketplace.review.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.freelancemarketplace.contract.entity.Contract;
import com.freelancemarketplace.contract.entity.ContractStatus;
import com.freelancemarketplace.contract.repository.ContractRepository;
import com.freelancemarketplace.exception.BadRequestException;
import com.freelancemarketplace.exception.ForbiddenException;
import com.freelancemarketplace.exception.ResourceAlreadyExistsException;
import com.freelancemarketplace.exception.ResourceNotFoundException;
import com.freelancemarketplace.review.dto.ReviewRequest;
import com.freelancemarketplace.review.dto.ReviewResponse;
import com.freelancemarketplace.review.entity.Review;
import com.freelancemarketplace.review.repository.ReviewRepository;
import com.freelancemarketplace.user.entity.User;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ContractRepository contractRepository;

    public ReviewService(
            ReviewRepository reviewRepository,
            ContractRepository contractRepository) {
        this.reviewRepository = reviewRepository;
        this.contractRepository = contractRepository;
    }

    @Transactional
    public ReviewResponse createReview(
            Long contractId,
            ReviewRequest request,
            Long reviewerId) {

        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() ->
                    new ResourceNotFoundException("Contract not found"));

        // Reviews are allowed only after contract completion.
        if (contract.getStatus() != ContractStatus.COMPLETED) {
            throw new BadRequestException(
                "Reviews can only be submitted for completed contracts");
        }

        User reviewer;
        User reviewee;

        // Client reviews freelancer.
        if (contract.getClient().getId().equals(reviewerId)) {
            reviewer = contract.getClient();
            reviewee = contract.getFreelancer();

        // Freelancer reviews client.
        } else if (contract.getFreelancer().getId().equals(reviewerId)) {
            reviewer = contract.getFreelancer();
            reviewee = contract.getClient();

        } else {
            throw new ForbiddenException(
                "Only participants of this contract can submit reviews");
        }

        if (reviewRepository.existsByContractIdAndReviewerId(
                contractId, reviewerId)) {
            throw new ResourceAlreadyExistsException(
                "You have already reviewed this contract");
        }

        Review review = new Review();
        review.setContract(contract);
        review.setReviewer(reviewer);
        review.setReviewee(reviewee);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review savedReview = reviewRepository.save(review);

        return mapToResponse(savedReview);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsGiven(Long userId) {

        return reviewRepository
                .findByReviewerIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsReceived(Long userId) {

        return reviewRepository
                .findByRevieweeIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ReviewResponse mapToResponse(Review review) {

        return new ReviewResponse(
            review.getId(),
            review.getContract().getId(),
            review.getContract().getProject().getId(),
            review.getReviewer().getId(),
            review.getReviewee().getId(),
            review.getRating(),
            review.getComment(),
            review.getCreatedAt()
        );
    }
}