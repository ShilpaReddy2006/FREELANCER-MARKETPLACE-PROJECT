package com.freelancemarketplace.project.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

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
            throw new RuntimeException(
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
    public ProjectResponse getProjectById(Long projectId) {

        Project project = projectRepository.findById(projectId)
            .orElseThrow(() ->
                new ResourceNotFoundException("Project not found"));

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
}