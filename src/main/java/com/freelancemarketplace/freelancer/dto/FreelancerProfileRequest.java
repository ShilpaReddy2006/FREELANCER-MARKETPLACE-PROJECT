package com.freelancemarketplace.freelancer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class FreelancerProfileRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String bio;

    private String skills;

    @Positive(message = "Hourly rate must be greater than 0")
    private Double hourlyRate;

    public FreelancerProfileRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public Double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(Double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }
}