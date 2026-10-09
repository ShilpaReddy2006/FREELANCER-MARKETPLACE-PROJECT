
package com.freelancemarketplace.contract.controller;

import com.freelancemarketplace.contract.dto.ContractRequest;
import com.freelancemarketplace.contract.dto.ContractResponse;
import com.freelancemarketplace.contract.service.ContractService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contracts")
public class ContractController {

    private final ContractService contractService;

    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ContractResponse> createContract(
            @Valid @RequestBody ContractRequest request,
            Authentication authentication) {

        Long clientId = (Long) authentication.getPrincipal();

        ContractResponse response =
                contractService.createContract(request, clientId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('CLIENT', 'FREELANCER')")
    public ResponseEntity<List<ContractResponse>> getMyContracts(
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
                        new IllegalStateException(
                                "No supported role found for authenticated user"));

        List<ContractResponse> contracts =
                contractService.getMyContracts(userId, role);

        return ResponseEntity.ok(contracts);
    }
}