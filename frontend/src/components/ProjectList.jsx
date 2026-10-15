import { useEffect, useState } from "react";
import axiosInstance from "../api/axiosInstance";
import "./ProjectList.css";

function ProjectList() {
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let active = true;

    async function fetchProjects() {
      try {
        setLoading(true);
        setError("");

        const response = await axiosInstance.get("/projects");

        // Supports either a plain array or common paginated responses.
        const data = response.data;
        const projectList = Array.isArray(data)
          ? data
          : Array.isArray(data?.content)
            ? data.content
            : Array.isArray(data?.projects)
              ? data.projects
              : Array.isArray(data?.data)
                ? data.data
                : null;

        if (!projectList) {
          throw new Error(
            "The projects API returned an unexpected response format."
          );
        }

        if (active) {
          setProjects(projectList);
        }
      } catch (err) {
        if (active) {
          setError(
            err.response?.data?.message ||
              err.message ||
              "Unable to load projects. Please try again."
          );
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    }

    fetchProjects();

    return () => {
      active = false;
    };
  }, []);

  if (loading) {
    return <div className="projects-message">Loading projects...</div>;
  }

  if (error) {
    return (
      <div className="projects-message projects-error">
        <p>{error}</p>
        <button
          type="button"
          onClick={() => window.location.reload()}
        >
          Try again
        </button>
      </div>
    );
  }

  if (projects.length === 0) {
    return (
      <div className="projects-message">
        No projects found. Check back later or create a project as a client.
      </div>
    );
  }

  return (
    <section className="real-projects">
      <div className="real-projects-heading">
        <div>
          <h2>Available Projects</h2>
          <p>Projects loaded from your marketplace backend</p>
        </div>
        <span className="project-count">{projects.length} projects</span>
      </div>

      <div className="real-projects-grid">
        {projects.map((project, index) => (
          <article
            className="real-project-card"
            key={project.id ?? project.projectId ?? index}
          >
            <div className="real-project-card-top">
              <span className="project-label">Marketplace project</span>
              {project.status && (
                <span className="project-status">{project.status}</span>
              )}
            </div>

            <h3>
              {project.title ??
                project.projectTitle ??
                project.name ??
                `Project ${project.id ?? project.projectId ?? index + 1}`}
            </h3>

            <p className="real-project-description">
              {project.description || "No description provided."}
            </p>

            <div className="real-project-details">
              <div>
                <span>Budget</span>
                <strong>
                  {project.budget != null
                    ? `₹${project.budget}`
                    : project.amount != null
                      ? `₹${project.amount}`
                      : project.minBudget != null
                        ? `₹${project.minBudget}${
                            project.maxBudget != null
                              ? ` – ₹${project.maxBudget}`
                              : ""
                          }`
                        : "Not specified"}
                </strong>
              </div>

              <div>
                <span>Deadline</span>
                <strong>
                  {project.deadline
                    ? String(project.deadline).slice(0, 10)
                    : project.endDate
                      ? String(project.endDate).slice(0, 10)
                      : "Not specified"}
                </strong>
              </div>
            </div>

            {project.requiredSkills && (
              <div className="project-skills">
                <span>Skills: </span>
                {Array.isArray(project.requiredSkills)
                  ? project.requiredSkills.join(", ")
                  : project.requiredSkills}
              </div>
            )}

            {project.skillsRequired && (
              <div className="project-skills">
                <span>Skills: </span>
                {Array.isArray(project.skillsRequired)
                  ? project.skillsRequired.join(", ")
                  : project.skillsRequired}
              </div>
            )}
          </article>
        ))}
      </div>
    </section>
  );
}

export default ProjectList;