package com.freelancemarketplace.project.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.freelancemarketplace.project.dto.ProjectRequest;
import com.freelancemarketplace.project.dto.ProjectResponse;
import com.freelancemarketplace.project.service.ProjectService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    // Create project
    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @Valid @RequestBody ProjectRequest request,
            Authentication authentication) {

        Long clientId =
                (Long) authentication.getPrincipal();

        ProjectResponse response =
                projectService.createProject(
                        clientId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Get all open projects
    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getOpenProjects() {

        List<ProjectResponse> projects =
                projectService.getOpenProjects();

        return ResponseEntity.ok(projects);
    }
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> getProjectById(
            @PathVariable Long projectId) {

        ProjectResponse response =
            projectService.getProjectById(projectId);

        return ResponseEntity.ok(response);
    }
}