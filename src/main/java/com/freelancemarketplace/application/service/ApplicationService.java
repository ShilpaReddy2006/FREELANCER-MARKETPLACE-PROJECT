package com.freelancemarketplace.application.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import java.util.List;

import com.freelancemarketplace.exception.ForbiddenException;

import com.freelancemarketplace.application.dto.ApplicationRequest;
import com.freelancemarketplace.application.dto.ApplicationResponse;
import com.freelancemarketplace.application.entity.Application;
import com.freelancemarketplace.application.entity.ApplicationStatus;
import com.freelancemarketplace.application.repository.ApplicationRepository;
import com.freelancemarketplace.exception.ResourceAlreadyExistsException;
import com.freelancemarketplace.exception.ResourceNotFoundException;
import com.freelancemarketplace.project.entity.Project;
import com.freelancemarketplace.project.entity.ProjectStatus;
import com.freelancemarketplace.project.repository.ProjectRepository;
import com.freelancemarketplace.user.entity.Role;
import com.freelancemarketplace.user.entity.User;
import com.freelancemarketplace.user.repository.UserRepository;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ApplicationService(
            ApplicationRepository applicationRepository,
            ProjectRepository projectRepository,
            UserRepository userRepository) {

        this.applicationRepository = applicationRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    // Freelancer applies to a project
    public ApplicationResponse applyToProject(
            Long freelancerId,
            Long projectId,
            ApplicationRequest request) {

        // 1. Find freelancer
        User freelancer = userRepository.findById(freelancerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Freelancer not found"));

        // 2. Check freelancer role
        if (freelancer.getRole() != Role.FREELANCER) {
            throw new RuntimeException(
                    "Only freelancers can apply to projects");
        }

        // 3. Find project
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found"));

        // 4. Project must be OPEN
        if (project.getStatus() != ProjectStatus.OPEN) {
            throw new RuntimeException(
                    "Applications are allowed only for open projects");
        }

        // 5. Check duplicate application
        if (applicationRepository
                .existsByProjectIdAndFreelancerId(
                        projectId,
                        freelancerId)) {

            throw new ResourceAlreadyExistsException(
                    "You have already applied to this project");
        }

        // 6. Create application
        Application application = new Application();

        application.setProject(project);
        application.setFreelancer(freelancer);
        application.setProposal(request.getProposal());
        application.setProposedBudget(
                request.getProposedBudget());

        // 7. Default status
        application.setStatus(ApplicationStatus.PENDING);

        // 8. Timestamps
        LocalDateTime now = LocalDateTime.now();

        application.setCreatedAt(now);
        application.setUpdatedAt(now);

        // 9. Save
        Application savedApplication =
                applicationRepository.save(application);

        // 10. Return response
        return new ApplicationResponse(
                savedApplication.getId(),
                savedApplication.getProject().getId(),
                savedApplication.getFreelancer().getId(),
                savedApplication.getProposal(),
                savedApplication.getProposedBudget(),
                savedApplication.getStatus(),
                savedApplication.getCreatedAt(),
                savedApplication.getUpdatedAt()
        );
    }
    public List<ApplicationResponse> getApplicationsForProject(
            Long clientId,
            Long projectId) {

        Project project = projectRepository.findById(projectId)
            .orElseThrow(() ->
                new ResourceNotFoundException("Project not found"));

        if (!project.getClient().getId().equals(clientId)) {
            throw new ForbiddenException(
                "You are not allowed to view applications for this project"
            );
        }

        List<Application> applications =
            applicationRepository.findByProjectId(projectId);

        return applications.stream()
            .map(application -> new ApplicationResponse(
                application.getId(),
                application.getProject().getId(),
                application.getFreelancer().getId(),
                application.getProposal(),
                application.getProposedBudget(),
                application.getStatus(),
                application.getCreatedAt(),
                application.getUpdatedAt()
            ))
            .toList();
    }
}