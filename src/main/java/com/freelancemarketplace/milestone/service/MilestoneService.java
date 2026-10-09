
package com.freelancemarketplace.milestone.service;

import com.freelancemarketplace.contract.entity.Contract;
import com.freelancemarketplace.contract.entity.ContractStatus;
import com.freelancemarketplace.contract.repository.ContractRepository;
import com.freelancemarketplace.exception.BadRequestException;
import com.freelancemarketplace.exception.ForbiddenException;
import com.freelancemarketplace.exception.ResourceNotFoundException;
import com.freelancemarketplace.milestone.dto.MilestoneRequest;
import com.freelancemarketplace.milestone.dto.MilestoneResponse;
import com.freelancemarketplace.milestone.entity.Milestone;
import com.freelancemarketplace.milestone.entity.MilestoneStatus;
import com.freelancemarketplace.milestone.repository.MilestoneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MilestoneService {

    private final MilestoneRepository milestoneRepository;
    private final ContractRepository contractRepository;

    public MilestoneService(
            MilestoneRepository milestoneRepository,
            ContractRepository contractRepository) {

        this.milestoneRepository = milestoneRepository;
        this.contractRepository = contractRepository;
    }

    @Transactional
    public MilestoneResponse createMilestone(
            Long contractId,
            MilestoneRequest request,
            Long clientId) {

        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Contract not found"));

        if (!contract.getClient().getId().equals(clientId)) {
            throw new ForbiddenException(
                    "Only the contract client can create milestones");
        }

        if (contract.getStatus() != ContractStatus.ACTIVE) {
            throw new BadRequestException(
                    "Milestones can only be added to active contracts");
        }

        List<Milestone> existing =
                milestoneRepository.findByContractIdOrderByIdAsc(contractId);

        double allocated = existing.stream()
                .mapToDouble(Milestone::getAmount)
                .sum();

        if (allocated + request.getAmount() > contract.getAgreedAmount()) {
            throw new BadRequestException(
                    "Total milestone amounts cannot exceed contract amount");
        }

        Milestone milestone = new Milestone();
        milestone.setContract(contract);
        milestone.setTitle(request.getTitle().trim());
        milestone.setDescription(request.getDescription());
        milestone.setAmount(request.getAmount());
        milestone.setDueDate(request.getDueDate());
        milestone.setStatus(MilestoneStatus.PENDING);

        return mapToResponse(milestoneRepository.save(milestone));
    }

    @Transactional(readOnly = true)
    public List<MilestoneResponse> getMilestones(
            Long contractId,
            Long userId) {

        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Contract not found"));

        boolean isClient =
                contract.getClient().getId().equals(userId);

        boolean isFreelancer =
                contract.getFreelancer().getId().equals(userId);

        if (!isClient && !isFreelancer) {
            throw new ForbiddenException(
                    "You cannot view milestones for this contract");
        }

        return milestoneRepository
                .findByContractIdOrderByIdAsc(contractId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public MilestoneResponse updateMilestoneStatus(
            Long milestoneId,
            MilestoneStatus newStatus,
            Long userId,
            String role) {

        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Milestone not found"));

        Contract contract = milestone.getContract();

        if (contract.getStatus() != ContractStatus.ACTIVE) {
            throw new BadRequestException(
                    "Milestones can only be updated for active contracts");
        }

        boolean isClient =
                contract.getClient().getId().equals(userId);

        boolean isFreelancer =
                contract.getFreelancer().getId().equals(userId);

        if ("CLIENT".equals(role) && !isClient) {
            throw new ForbiddenException(
                    "You are not the client for this contract");
        }

        if ("FREELANCER".equals(role) && !isFreelancer) {
            throw new ForbiddenException(
                    "You are not the freelancer for this contract");
        }

        if (newStatus == null) {
            throw new BadRequestException("Milestone status is required");
        }

        MilestoneStatus current = milestone.getStatus();

        boolean allowed = false;

        if ("FREELANCER".equals(role)) {
            allowed = (current == MilestoneStatus.PENDING
                    && newStatus == MilestoneStatus.IN_PROGRESS)
                    || ((current == MilestoneStatus.IN_PROGRESS
                    || current == MilestoneStatus.REJECTED)
                    && newStatus == MilestoneStatus.SUBMITTED);
        }

        if ("CLIENT".equals(role)) {
            allowed = current == MilestoneStatus.SUBMITTED
                    && (newStatus == MilestoneStatus.APPROVED
                    || newStatus == MilestoneStatus.REJECTED);
        }

        if (!allowed) {
            throw new BadRequestException(
                    "Invalid milestone status transition for your role");
        }

        milestone.setStatus(newStatus);

        return mapToResponse(milestoneRepository.save(milestone));
    }

    private MilestoneResponse mapToResponse(Milestone milestone) {
        return new MilestoneResponse(
                milestone.getId(),
                milestone.getContract().getId(),
                milestone.getTitle(),
                milestone.getDescription(),
                milestone.getAmount(),
                milestone.getDueDate(),
                milestone.getStatus(),
                milestone.getCreatedAt(),
                milestone.getUpdatedAt()
        );
    }
}