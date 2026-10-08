package com.freelancemarketplace.contract.repository;

import com.freelancemarketplace.contract.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import com.freelancemarketplace.contract.entity.ContractStatus;

public interface ContractRepository extends JpaRepository<Contract, Long> {

    Optional<Contract> findByApplicationId(Long applicationId);

    boolean existsByProjectIdAndStatus(
            Long projectId,
            ContractStatus status
    );
}