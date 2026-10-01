package com.freelancemarketplace.freelancer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.freelancemarketplace.freelancer.entity.FreelancerProfile;

public interface FreelancerProfileRepository
        extends JpaRepository<FreelancerProfile, Long> {

    Optional<FreelancerProfile> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}