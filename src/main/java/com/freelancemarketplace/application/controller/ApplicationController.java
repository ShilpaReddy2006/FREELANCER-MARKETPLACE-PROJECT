package com.freelancemarketplace.application.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.freelancemarketplace.application.dto.ApplicationRequest;
import com.freelancemarketplace.application.dto.ApplicationResponse;
import com.freelancemarketplace.application.service.ApplicationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/projects")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(
            ApplicationService applicationService) {

        this.applicationService = applicationService;
    }

    @PostMapping("/{projectId}/applications")
    public ResponseEntity<ApplicationResponse> applyToProject(
            @PathVariable Long projectId,
            @Valid @RequestBody ApplicationRequest request,
            Authentication authentication) {

        Long freelancerId =
                (Long) authentication.getPrincipal();

        ApplicationResponse response =
                applicationService.applyToProject(
                        freelancerId,
                        projectId,
                        request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{projectId}/applications")
    public ResponseEntity<List<ApplicationResponse>>
            getApplicationsForProject(
                    @PathVariable Long projectId,
                    Authentication authentication) {

        Long clientId =
                (Long) authentication.getPrincipal();

        List<ApplicationResponse> applications =
                applicationService.getApplicationsForProject(
                        clientId,
                        projectId);

        return ResponseEntity.ok(applications);
    }

    @PostMapping("/{projectId}/applications/{applicationId}/accept")
    public ResponseEntity<ApplicationResponse> acceptApplication(
            @PathVariable Long projectId,
            @PathVariable Long applicationId,
            Authentication authentication) {

        Long clientId =
                (Long) authentication.getPrincipal();

        ApplicationResponse response =
                applicationService.acceptApplication(
                        clientId,
                        projectId,
                        applicationId);

        return ResponseEntity.ok(response);
    }
}