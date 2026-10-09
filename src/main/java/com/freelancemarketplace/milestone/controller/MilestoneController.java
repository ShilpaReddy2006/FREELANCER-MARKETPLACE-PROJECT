
package com.freelancemarketplace.milestone.controller;

import com.freelancemarketplace.milestone.dto.MilestoneRequest;
import com.freelancemarketplace.milestone.dto.MilestoneResponse;
import com.freelancemarketplace.milestone.dto.MilestoneStatusRequest;
import com.freelancemarketplace.milestone.service.MilestoneService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MilestoneController {

    private final MilestoneService milestoneService;

    public MilestoneController(MilestoneService milestoneService) {
        this.milestoneService = milestoneService;
    }

    @PostMapping("/api/contracts/{contractId}/milestones")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<MilestoneResponse> createMilestone(
            @PathVariable Long contractId,
            @Valid @RequestBody MilestoneRequest request,
            Authentication authentication) {

        Long clientId = (Long) authentication.getPrincipal();

        MilestoneResponse response =
                milestoneService.createMilestone(
                        contractId, request, clientId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/contracts/{contractId}/milestones")
    @PreAuthorize("hasAnyRole('CLIENT', 'FREELANCER')")
    public ResponseEntity<List<MilestoneResponse>> getMilestones(
            @PathVariable Long contractId,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();

        List<MilestoneResponse> response =
                milestoneService.getMilestones(contractId, userId);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/api/milestones/{milestoneId}/status")
    @PreAuthorize("hasAnyRole('CLIENT', 'FREELANCER')")
    public ResponseEntity<MilestoneResponse> updateMilestoneStatus(
            @PathVariable Long milestoneId,
            @Valid @RequestBody MilestoneStatusRequest request,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();

        String role = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority ->
                        authority.equals("ROLE_CLIENT")
                                || authority.equals("ROLE_FREELANCER"))
                .map(authority -> authority.substring("ROLE_".length()))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException("No supported role found"));

        MilestoneResponse response =
                milestoneService.updateMilestoneStatus(
                        milestoneId,
                        request.getStatus(),
                        userId,
                        role);

        return ResponseEntity.ok(response);
    }
}