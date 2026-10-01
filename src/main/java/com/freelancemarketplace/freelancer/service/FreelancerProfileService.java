package com.freelancemarketplace.freelancer.service;

import org.springframework.stereotype.Service;

import com.freelancemarketplace.freelancer.dto.FreelancerProfileRequest;
import com.freelancemarketplace.freelancer.dto.FreelancerProfileResponse;
import com.freelancemarketplace.freelancer.entity.FreelancerProfile;
import com.freelancemarketplace.freelancer.repository.FreelancerProfileRepository;
import com.freelancemarketplace.user.entity.User;
import com.freelancemarketplace.user.repository.UserRepository;

@Service
public class FreelancerProfileService {

    private final FreelancerProfileRepository freelancerProfileRepository;
    private final UserRepository userRepository;

    public FreelancerProfileService(
            FreelancerProfileRepository freelancerProfileRepository,
            UserRepository userRepository) {

        this.freelancerProfileRepository = freelancerProfileRepository;
        this.userRepository = userRepository;
    }

    public FreelancerProfileResponse createProfile(
            Long userId,
            FreelancerProfileRequest request) {

        // Check whether user already has a freelancer profile
        if (freelancerProfileRepository.existsByUserId(userId)) {
            throw new RuntimeException(
                    "Freelancer profile already exists");
        }

        // Find the authenticated user
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // Create freelancer profile
        FreelancerProfile profile = new FreelancerProfile();

        profile.setUser(user);
        profile.setTitle(request.getTitle());
        profile.setBio(request.getBio());
        profile.setSkills(request.getSkills());
        profile.setHourlyRate(request.getHourlyRate());

        // Save profile
        FreelancerProfile savedProfile =
                freelancerProfileRepository.save(profile);

        // Return response DTO
        return new FreelancerProfileResponse(
                savedProfile.getId(),
                savedProfile.getUser().getId(),
                savedProfile.getTitle(),
                savedProfile.getBio(),
                savedProfile.getSkills(),
                savedProfile.getHourlyRate()
        );
    }

    public FreelancerProfileResponse getProfile(Long userId) {

        FreelancerProfile profile =
                freelancerProfileRepository
                        .findByUserId(userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Freelancer profile not found"));

        return new FreelancerProfileResponse(
                profile.getId(),
                profile.getUser().getId(),
                profile.getTitle(),
                profile.getBio(),
                profile.getSkills(),
                profile.getHourlyRate()
        );
    }
}