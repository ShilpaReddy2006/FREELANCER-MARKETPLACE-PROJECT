
package com.freelancemarketplace.contract.dto;

import com.freelancemarketplace.contract.entity.ContractStatus;
import jakarta.validation.constraints.NotNull;

public class ContractStatusRequest {

    @NotNull(message = "Contract status is required")
    private ContractStatus status;

    public ContractStatus getStatus() {
        return status;
    }

    public void setStatus(ContractStatus status) {
        this.status = status;
    }
}