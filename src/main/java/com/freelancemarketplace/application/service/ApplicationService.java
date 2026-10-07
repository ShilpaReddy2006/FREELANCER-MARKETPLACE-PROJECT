package com.freelancemarketplace.application.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.freelancemarketplace.application.dto.ApplicationRequest;
import com.freelancemarketplace.application.dto.ApplicationResponse;
import com.freelancemarketplace.application.entity.Application;
import com.freelancemarketplace.application.entity.ApplicationStatus;
import com.freelancemarketplace.application.repository.ApplicationRepository;
import com.freelancemarketplace.exception.BadRequestException;
import com.freelancemarketplace.exception.ForbiddenException;
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

            throw new BadRequestException(
                    "Only freelancers can apply to projects");
        }

        // 3. Find project
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found"));

        // 4. Project must be OPEN
        if (project.getStatus() != ProjectStatus.OPEN) {

            throw new BadRequestException(
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

    // Client views applications for their project
    public List<ApplicationResponse> getApplicationsForProject(
            Long clientId,
            Long projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found"));

        if (!project.getClient().getId().equals(clientId)) {

            throw new ForbiddenException(
                    "You are not allowed to view applications for this project");
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

    // Client accepts an application
    @Transactional
    public ApplicationResponse acceptApplication(
            Long clientId,
            Long projectId,
            Long applicationId) {

        // 1. Find the project
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found"));

        // 2. Check whether logged-in client owns the project
        if (!project.getClient().getId().equals(clientId)) {

            throw new ForbiddenException(
                    "You are not allowed to manage applications for this project");
        }

        // 3. Project must be OPEN
        if (project.getStatus() != ProjectStatus.OPEN) {

            throw new BadRequestException(
                    "Applications can only be accepted for open projects");
        }

        // 4. Find the application
        Application application =
                applicationRepository.findById(applicationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Application not found"));

        // 5. Make sure application belongs to this project
        if (!application.getProject().getId().equals(projectId)) {

            throw new ForbiddenException(
                    "This application does not belong to this project");
        }

        // 6. Application must be PENDING
        if (application.getStatus() != ApplicationStatus.PENDING) {

            throw new BadRequestException(
                    "Only pending applications can be accepted");
        }

        // 7. Accept selected application
        application.setStatus(ApplicationStatus.ACCEPTED);
        application.setUpdatedAt(LocalDateTime.now());

        // 8. Project becomes IN_PROGRESS
        project.setStatus(ProjectStatus.IN_PROGRESS);
        project.setUpdatedAt(LocalDateTime.now());

        projectRepository.save(project);

        // 9. Reject all other pending applications
        List<Application> pendingApplications =
                applicationRepository.findByProjectIdAndStatus(
                        projectId,
                        ApplicationStatus.PENDING);

        for (Application pendingApplication : pendingApplications) {

            if (!pendingApplication.getId().equals(applicationId)) {

                pendingApplication.setStatus(
                        ApplicationStatus.REJECTED);

                pendingApplication.setUpdatedAt(
                        LocalDateTime.now());

                applicationRepository.save(pendingApplication);
            }
        }

        // 10. Save accepted application
        Application savedApplication =
                applicationRepository.save(application);

        // 11. Return response
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
 // Get applications submitted by logged-in freelancer
    public List<ApplicationResponse> getMyApplications(
            Long freelancerId) {

        // 1. Find all applications of this freelancer
        List<Application> applications =
                applicationRepository.findByFreelancerId(freelancerId);

        // 2. Convert entities to responses
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