package com.freelancemarketplace.project.service;

import java.time.LocalDateTime;
import java.util.List;
import com.freelancemarketplace.exception.ForbiddenException;
import org.springframework.stereotype.Service;

import com.freelancemarketplace.exception.BadRequestException;
import com.freelancemarketplace.exception.ResourceNotFoundException;
import com.freelancemarketplace.project.dto.ProjectRequest;
import com.freelancemarketplace.project.dto.ProjectResponse;
import com.freelancemarketplace.project.entity.Project;
import com.freelancemarketplace.project.entity.ProjectStatus;
import com.freelancemarketplace.project.repository.ProjectRepository;
import com.freelancemarketplace.user.entity.Role;
import com.freelancemarketplace.user.entity.User;
import com.freelancemarketplace.user.repository.UserRepository;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectService(
            ProjectRepository projectRepository,
            UserRepository userRepository) {

        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    // Create project
    public ProjectResponse createProject(
            Long clientId,
            ProjectRequest request) {

        // 1. Find logged-in user
        User client = userRepository.findById(clientId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"));

        // 2. Check CLIENT role
        if (client.getRole() != Role.CLIENT) {

            throw new BadRequestException(
                    "Only clients can create projects");
        }

        // 3. Create Project object
        Project project = new Project();

        project.setClient(client);
        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());
        project.setBudget(request.getBudget());
        project.setDeadline(request.getDeadline());

        // 4. Set default status
        project.setStatus(ProjectStatus.OPEN);

        // 5. Set timestamps
        LocalDateTime now = LocalDateTime.now();

        project.setCreatedAt(now);
        project.setUpdatedAt(now);

        // 6. Save project
        Project savedProject =
                projectRepository.save(project);

        // 7. Return response
        return new ProjectResponse(
                savedProject.getId(),
                savedProject.getClient().getId(),
                savedProject.getTitle(),
                savedProject.getDescription(),
                savedProject.getBudget(),
                savedProject.getDeadline(),
                savedProject.getStatus(),
                savedProject.getCreatedAt(),
                savedProject.getUpdatedAt()
        );
    }

    // Get all open projects
    public List<ProjectResponse> getOpenProjects() {

        List<Project> projects =
                projectRepository.findByStatus(ProjectStatus.OPEN);

        return projects.stream()
                .map(project -> new ProjectResponse(
                        project.getId(),
                        project.getClient().getId(),
                        project.getTitle(),
                        project.getDescription(),
                        project.getBudget(),
                        project.getDeadline(),
                        project.getStatus(),
                        project.getCreatedAt(),
                        project.getUpdatedAt()
                ))
                .toList();
    }

    // Get project by ID
    public ProjectResponse getProjectById(Long projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found"));

        return new ProjectResponse(
                project.getId(),
                project.getClient().getId(),
                project.getTitle(),
                project.getDescription(),
                project.getBudget(),
                project.getDeadline(),
                project.getStatus(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }
 // Complete project
    public ProjectResponse completeProject(
            Long clientId,
            Long projectId) {

        // 1. Find project
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found"));

        // 2. Check project ownership
        if (!project.getClient().getId().equals(clientId)) {

            throw new ForbiddenException(
                    "You are not allowed to complete this project");
        }

        // 3. Project must be IN_PROGRESS
        if (project.getStatus() != ProjectStatus.IN_PROGRESS) {

            throw new BadRequestException(
                    "Only in-progress projects can be completed");
        }

        // 4. Change status
        project.setStatus(ProjectStatus.COMPLETED);

        // 5. Update timestamp
        project.setUpdatedAt(LocalDateTime.now());

        // 6. Save project
        Project savedProject =
                projectRepository.save(project);

        // 7. Return response
        return new ProjectResponse(
                savedProject.getId(),
                savedProject.getClient().getId(),
                savedProject.getTitle(),
                savedProject.getDescription(),
                savedProject.getBudget(),
                savedProject.getDeadline(),
                savedProject.getStatus(),
                savedProject.getCreatedAt(),
                savedProject.getUpdatedAt()
        );
    }
}