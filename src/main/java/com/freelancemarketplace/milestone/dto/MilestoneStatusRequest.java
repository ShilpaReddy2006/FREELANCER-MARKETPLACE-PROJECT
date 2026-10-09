
package com.freelancemarketplace.milestone.dto;

import com.freelancemarketplace.milestone.entity.MilestoneStatus;
import jakarta.validation.constraints.NotNull;

public class MilestoneStatusRequest {

    @NotNull(message = "Milestone status is required")
    private MilestoneStatus status;

    public MilestoneStatus getStatus() {
        return status;
    }

    public void setStatus(MilestoneStatus status) {
        this.status = status;
    }
}