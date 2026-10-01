package com.freelancemarketplace.freelancer.dto;

public class FreelancerProfileResponse {

    private Long id;
    private Long userId;
    private String title;
    private String bio;
    private String skills;
    private Double hourlyRate;

    public FreelancerProfileResponse() {
    }

    public FreelancerProfileResponse(
            Long id,
            Long userId,
            String title,
            String bio,
            String skills,
            Double hourlyRate) {

        this.id = id;
        this.userId = userId;
        this.title = title;
        this.bio = bio;
        this.skills = skills;
        this.hourlyRate = hourlyRate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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