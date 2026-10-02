package com.freelancemarketplace.freelancer.controller;

import com.freelancemarketplace.freelancer.dto.FreelancerProfileRequest;
import com.freelancemarketplace.freelancer.dto.FreelancerProfileResponse;
import com.freelancemarketplace.freelancer.service.FreelancerProfileService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/freelancer/profile")
public class FreelancerProfileController {

    private final FreelancerProfileService freelancerProfileService;

    public FreelancerProfileController(
            FreelancerProfileService freelancerProfileService) {

        this.freelancerProfileService = freelancerProfileService;
    }

    // Create freelancer profile
    @PostMapping
    public ResponseEntity<FreelancerProfileResponse> createProfile(
            @Valid @RequestBody FreelancerProfileRequest request,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();

        FreelancerProfileResponse response =
                freelancerProfileService.createProfile(
                        userId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Get logged-in user's profile
    @GetMapping
    public ResponseEntity<FreelancerProfileResponse> getProfile(
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();

        FreelancerProfileResponse response =
                freelancerProfileService.getProfile(userId);

        return ResponseEntity.ok(response);
    }

    // Get freelancer profile by user ID
    @GetMapping("/{userId}")
    public ResponseEntity<FreelancerProfileResponse> getProfileByUserId(
            @PathVariable Long userId) {

        FreelancerProfileResponse response =
                freelancerProfileService.getProfile(userId);

        return ResponseEntity.ok(response);
    }
}