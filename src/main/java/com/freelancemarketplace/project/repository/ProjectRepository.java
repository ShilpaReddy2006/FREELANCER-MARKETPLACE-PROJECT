package com.freelancemarketplace.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.freelancemarketplace.project.entity.Project;
import com.freelancemarketplace.project.entity.ProjectStatus;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByStatus(ProjectStatus status);
}