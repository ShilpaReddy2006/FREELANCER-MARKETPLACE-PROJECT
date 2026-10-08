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

        // 1. Find the application
        Application application =
                applicationRepository.findById(request.getApplicationId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Application not found"));

        // 2. Application must be ACCEPTED
        if (application.getStatus() != ApplicationStatus.ACCEPTED) {
            throw new BadRequestException(
                    "Contract can only be created from an accepted application");
        }

        // 3. Get the project
        var project = application.getProject();

        // 4. Verify that logged-in user owns the project
        if (!project.getClient().getId().equals(clientId)) {
            throw new ForbiddenException(
                    "Only the project owner can create a contract");
        }

        // 5. Check if project already has an active contract
        if (contractRepository.existsByProjectIdAndStatus(
                project.getId(),
                ContractStatus.ACTIVE)) {

            throw new ResourceAlreadyExistsException(
                    "Project already has an active contract");
        }

        // 6. Validate dates
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException(
                    "End date cannot be before start date");
        }

        // 7. Get client and freelancer
        User client = project.getClient();
        User freelancer = application.getFreelancer();

        // 8. Create contract
        Contract contract = new Contract();

        contract.setApplication(application);
        contract.setProject(project);
        contract.setClient(client);
        contract.setFreelancer(freelancer);

        // Use the accepted application's proposed budget
        contract.setAgreedAmount(
                application.getProposedBudget());

        contract.setStartDate(request.getStartDate());
        contract.setEndDate(request.getEndDate());

        contract.setStatus(ContractStatus.ACTIVE);

        // 9. Save contract
        Contract savedContract =
                contractRepository.save(contract);

        // 10. Convert entity to response
        return new ContractResponse(
                savedContract.getId(),
                savedContract.getProject().getId(),
                savedContract.getApplication().getId(),
                savedContract.getClient().getId(),
                savedContract.getFreelancer().getId(),
                savedContract.getAgreedAmount(),
                savedContract.getStartDate(),
                savedContract.getEndDate(),
                savedContract.getStatus(),
                savedContract.getCreatedAt(),
                savedContract.getUpdatedAt()
        );
    }
}