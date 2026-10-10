
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
import com.freelancemarketplace.notification.service.NotificationService;
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
    private final NotificationService notificationService;

    public ApplicationService(
            ApplicationRepository applicationRepository,
            ProjectRepository projectRepository,
            UserRepository userRepository,
            NotificationService notificationService) {

        this.applicationRepository = applicationRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    // Freelancer applies to a project
    @Transactional
    public ApplicationResponse applyToProject(
            Long freelancerId,
            Long projectId,
            ApplicationRequest request) {

        User freelancer = userRepository.findById(freelancerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Freelancer not found"));

        if (freelancer.getRole() != Role.FREELANCER) {
            throw new BadRequestException(
                    "Only freelancers can apply to projects");
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));

        if (project.getStatus() != ProjectStatus.OPEN) {
            throw new BadRequestException(
                    "Applications are allowed only for open projects");
        }

        if (applicationRepository.existsByProjectIdAndFreelancerId(
                projectId, freelancerId)) {
            throw new ResourceAlreadyExistsException(
                    "You have already applied to this project");
        }

        Application application = new Application();
        application.setProject(project);
        application.setFreelancer(freelancer);
        application.setProposal(request.getProposal());
        application.setProposedBudget(request.getProposedBudget());
        application.setStatus(ApplicationStatus.PENDING);

        LocalDateTime now = LocalDateTime.now();
        application.setCreatedAt(now);
        application.setUpdatedAt(now);

        Application savedApplication =
                applicationRepository.save(application);

        // Notify the project owner.
        notificationService.createNotification(
                project.getClient().getId(),
                "New Application",
                "A freelancer has applied to your project: "
                        + project.getTitle(),
                "NEW_APPLICATION"
        );

        return mapToResponse(savedApplication);
    }

    // Client views applications for their project
    @Transactional(readOnly = true)
    public List<ApplicationResponse> getApplicationsForProject(
            Long clientId,
            Long projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));

        if (!project.getClient().getId().equals(clientId)) {
            throw new ForbiddenException(
                    "You are not allowed to view applications for this project");
        }

        return applicationRepository.findByProjectId(projectId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Client accepts one application and rejects other pending applications.
    @Transactional
    public ApplicationResponse acceptApplication(
            Long clientId,
            Long projectId,
            Long applicationId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));

        if (!project.getClient().getId().equals(clientId)) {
            throw new ForbiddenException(
                    "You are not allowed to manage applications for this project");
        }

        if (project.getStatus() != ProjectStatus.OPEN) {
            throw new BadRequestException(
                    "Applications can only be accepted for open projects");
        }

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Application not found"));

        if (!application.getProject().getId().equals(projectId)) {
            throw new ForbiddenException(
                    "This application does not belong to this project");
        }

        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new BadRequestException(
                    "Only pending applications can be accepted");
        }

        // Accept the selected application.
        application.setStatus(ApplicationStatus.ACCEPTED);
        application.setUpdatedAt(LocalDateTime.now());

        // Project becomes IN_PROGRESS.
        project.setStatus(ProjectStatus.IN_PROGRESS);
        project.setUpdatedAt(LocalDateTime.now());
        projectRepository.save(project);

        // Reject other pending applications and notify each freelancer.
        List<Application> pendingApplications =
                applicationRepository.findByProjectIdAndStatus(
                        projectId, ApplicationStatus.PENDING);

        for (Application pendingApplication : pendingApplications) {

            if (!pendingApplication.getId().equals(applicationId)) {

                pendingApplication.setStatus(ApplicationStatus.REJECTED);
                pendingApplication.setUpdatedAt(LocalDateTime.now());

                Application rejectedApplication =
                        applicationRepository.save(pendingApplication);

                notificationService.createNotification(
                        rejectedApplication.getFreelancer().getId(),
                        "Application Rejected",
                        "Your application for project '"
                                + project.getTitle()
                                + "' was not selected.",
                        "APPLICATION_REJECTED"
                );
            }
        }

        Application savedApplication =
                applicationRepository.save(application);

        // Notify the selected freelancer.
        notificationService.createNotification(
                savedApplication.getFreelancer().getId(),
                "Application Accepted",
                "Your application for project '"
                        + project.getTitle()
                        + "' has been accepted.",
                "APPLICATION_ACCEPTED"
        );

        return mapToResponse(savedApplication);
    }

    // Freelancer views their own applications.
    @Transactional(readOnly = true)
    public List<ApplicationResponse> getMyApplications(
            Long freelancerId) {

        return applicationRepository.findByFreelancerId(freelancerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Convert Application entity into response DTO.
    private ApplicationResponse mapToResponse(Application application) {

        return new ApplicationResponse(
                application.getId(),
                application.getProject().getId(),
                application.getFreelancer().getId(),
                application.getProposal(),
                application.getProposedBudget(),
                application.getStatus(),
                application.getCreatedAt(),
                application.getUpdatedAt()
        );
    }
}
