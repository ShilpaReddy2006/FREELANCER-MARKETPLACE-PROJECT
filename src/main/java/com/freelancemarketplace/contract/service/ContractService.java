
package com.freelancemarketplace.contract.service;

import com.freelancemarketplace.application.entity.Application;
import com.freelancemarketplace.application.entity.ApplicationStatus;
import com.freelancemarketplace.application.repository.ApplicationRepository;
import com.freelancemarketplace.contract.dto.ContractRequest;
import com.freelancemarketplace.contract.dto.ContractResponse;
import com.freelancemarketplace.contract.entity.Contract;
import com.freelancemarketplace.contract.entity.ContractStatus;
import com.freelancemarketplace.contract.repository.ContractRepository;
import com.freelancemarketplace.exception.BadRequestException;
import com.freelancemarketplace.exception.ForbiddenException;
import com.freelancemarketplace.exception.ResourceAlreadyExistsException;
import com.freelancemarketplace.exception.ResourceNotFoundException;
import com.freelancemarketplace.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ContractService {

    private final ContractRepository contractRepository;
    private final ApplicationRepository applicationRepository;

    public ContractService(
            ContractRepository contractRepository,
            ApplicationRepository applicationRepository) {

        this.contractRepository = contractRepository;
        this.applicationRepository = applicationRepository;
    }

    @Transactional
    public ContractResponse createContract(
            ContractRequest request,
            Long clientId) {

        Application application =
                applicationRepository.findById(request.getApplicationId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Application not found"));

        if (application.getStatus() != ApplicationStatus.ACCEPTED) {
            throw new BadRequestException(
                    "Contract can only be created from an accepted application");
        }

        var project = application.getProject();

        if (!project.getClient().getId().equals(clientId)) {
            throw new ForbiddenException(
                    "Only the project owner can create a contract");
        }

        if (contractRepository.existsByProjectIdAndStatus(
                project.getId(),
                ContractStatus.ACTIVE)) {

            throw new ResourceAlreadyExistsException(
                    "Project already has an active contract");
        }

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException(
                    "End date cannot be before start date");
        }

        User client = project.getClient();
        User freelancer = application.getFreelancer();

        Contract contract = new Contract();

        contract.setApplication(application);
        contract.setProject(project);
        contract.setClient(client);
        contract.setFreelancer(freelancer);
        contract.setAgreedAmount(application.getProposedBudget());
        contract.setStartDate(request.getStartDate());
        contract.setEndDate(request.getEndDate());
        contract.setStatus(ContractStatus.ACTIVE);

        Contract savedContract = contractRepository.save(contract);

        return mapToResponse(savedContract);
    }

    // Get contracts belonging to the logged-in user
    @Transactional(readOnly = true)
    public List<ContractResponse> getMyContracts(
            Long userId,
            String role) {

        List<Contract> contracts;

        if ("CLIENT".equals(role)) {
            contracts = contractRepository.findByClientId(userId);
        } else if ("FREELANCER".equals(role)) {
            contracts = contractRepository.findByFreelancerId(userId);
        } else {
            throw new ForbiddenException(
                    "Only clients and freelancers can view contracts");
        }

        return contracts.stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Convert Contract entity into response DTO
    private ContractResponse mapToResponse(Contract contract) {

        return new ContractResponse(
                contract.getId(),
                contract.getProject().getId(),
                contract.getApplication().getId(),
                contract.getClient().getId(),
                contract.getFreelancer().getId(),
                contract.getAgreedAmount(),
                contract.getStartDate(),
                contract.getEndDate(),
                contract.getStatus(),
                contract.getCreatedAt(),
                contract.getUpdatedAt()
        );
    }
}