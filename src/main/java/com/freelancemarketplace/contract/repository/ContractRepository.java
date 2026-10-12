
package com.freelancemarketplace.contract.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.freelancemarketplace.contract.entity.Contract;
import com.freelancemarketplace.contract.entity.ContractStatus;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

    List<Contract> findByClientId(Long clientId);

    List<Contract> findByFreelancerId(Long freelancerId);

    List<Contract> findByApplicationId(Long applicationId);

    boolean existsByApplicationId(Long applicationId);

    boolean existsByProjectIdAndStatus(
            Long projectId,
            ContractStatus status);
}
