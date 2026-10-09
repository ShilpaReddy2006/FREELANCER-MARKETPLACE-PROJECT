
package com.freelancemarketplace.contract.repository;

import com.freelancemarketplace.contract.entity.Contract;
import com.freelancemarketplace.contract.entity.ContractStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContractRepository extends JpaRepository<Contract, Long> {

    Optional<Contract> findByApplicationId(Long applicationId);

    boolean existsByProjectIdAndStatus(
            Long projectId,
            ContractStatus status
    );

    List<Contract> findByClientId(Long clientId);

    List<Contract> findByFreelancerId(Long freelancerId);
}