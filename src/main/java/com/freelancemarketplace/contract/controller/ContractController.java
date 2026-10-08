package com.freelancemarketplace.contract.controller;

import com.freelancemarketplace.contract.dto.ContractRequest;
import com.freelancemarketplace.contract.dto.ContractResponse;
import com.freelancemarketplace.contract.service.ContractService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

        Long clientId =
                (Long) authentication.getPrincipal();

        ContractResponse response =
                contractService.createContract(
                        request,
                        clientId);

        return ResponseEntity.ok(response);
    }
}