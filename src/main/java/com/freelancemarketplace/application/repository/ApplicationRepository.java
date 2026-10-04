package com.freelancemarketplace.application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.freelancemarketplace.application.entity.Application;
import com.freelancemarketplace.application.entity.ApplicationStatus;

public interface ApplicationRepository
        extends JpaRepository<Application, Long> {

    boolean existsByProjectIdAndFreelancerId(
            Long projectId,
            Long freelancerId);

    List<Application> findByProjectId(Long projectId);

    List<Application> findByProjectIdAndStatus(
            Long projectId,
            ApplicationStatus status);
}