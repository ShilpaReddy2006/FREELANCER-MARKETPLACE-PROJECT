
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import axiosInstance from "../api/axiosInstance";
import "./CreateProject.css";

function getToday() {
  const today = new Date();
  const year = today.getFullYear();
  const month = String(today.getMonth() + 1).padStart(2, "0");
  const day = String(today.getDate()).padStart(2, "0");

  return `${year}-${month}-${day}`;
}

export default function CreateProject({ onCreated, onCancel }) {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    title: "",
    description: "",
    budget: "",
    deadline: "",
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  function handleChange(event) {
    const { name, value } = event.target;

    setFormData((previous) => ({
      ...previous,
      [name]: value,
    }));

    setError("");
    setSuccess("");
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setError("");
    setSuccess("");

    const title = formData.title.trim();
    const description = formData.description.trim();
    const budget = Number(formData.budget);

    if (!title || !description || !formData.deadline) {
      setError("Please fill in all fields.");
      return;
    }

    if (!Number.isFinite(budget) || budget <= 0) {
      setError("Budget must be greater than zero.");
      return;
    }

    if (formData.deadline < getToday()) {
      setError("Deadline cannot be in the past.");
      return;
    }

    const payload = {
      title,
      description,
      budget,
      deadline: formData.deadline,
    };

    try {
      setLoading(true);

      const response = await axiosInstance.post(
        "/projects",
        payload
      );

      setSuccess("Project created successfully!");

      setFormData({
        title: "",
        description: "",
        budget: "",
        deadline: "",
      });

      if (onCreated) {
        onCreated(response.data);
      }
    } catch (err) {
      const status = err.response?.status;
      const data = err.response?.data;

      if (status === 400) {
        setError(
          data?.message ||
            "Invalid project details. Please check your inputs."
        );
      } else if (status === 401) {
        setError("Your session has expired. Please sign in again.");
      } else if (status === 403) {
        setError("Only clients can create projects.");
      } else if (status === 409) {
        setError(data?.message || "The project could not be created.");
      } else {
        setError(
          data?.message ||
            "Unable to create the project. Please try again."
        );
      }
    } finally {
      setLoading(false);
    }
  }

  function handleCancel() {
    if (onCancel) {
      onCancel();
    } else {
      navigate("/dashboard");
    }
  }

  return (
    <div className="create-project-page">
      <div className="create-project-heading">
        <div>
          <p className="create-project-eyebrow">CLIENT WORKSPACE</p>
          <h1>Create a Project</h1>
          <p className="create-project-subtitle">
            Describe the work you need done and set your budget
            and deadline.
          </p>
        </div>
      </div>

      <form className="create-project-form" onSubmit={handleSubmit}>
        <div className="create-project-form-heading">
          <h2>Project details</h2>
          <p>Fields marked with * are required.</p>
        </div>

        {error && (
          <div className="project-message project-error" role="alert">
            {error}
          </div>
        )}

        {success && (
          <div className="project-message project-success" role="status">
            {success}
          </div>
        )}

        <div className="project-form-field">
          <label htmlFor="project-title">Project title *</label>
          <input
            id="project-title"
            name="title"
            type="text"
            placeholder="e.g. Build a responsive company website"
            value={formData.title}
            onChange={handleChange}
            maxLength={255}
            required
          />
        </div>

        <div className="project-form-field">
          <label htmlFor="project-description">Description *</label>
          <textarea
            id="project-description"
            name="description"
            placeholder="Explain the project requirements, expected work and deliverables..."
            value={formData.description}
            onChange={handleChange}
            rows={6}
            maxLength={2000}
            required
          />
          <div className="project-character-count">
            {formData.description.length}/2000 characters
          </div>
        </div>

        <div className="project-form-row">
          <div className="project-form-field">
            <label htmlFor="project-budget">Budget (₹) *</label>
            <input
              id="project-budget"
              name="budget"
              type="number"
              placeholder="e.g. 15000"
              value={formData.budget}
              onChange={handleChange}
              min="0.01"
              step="0.01"
              required
            />
          </div>

          <div className="project-form-field">
            <label htmlFor="project-deadline">Deadline *</label>
            <input
              id="project-deadline"
              name="deadline"
              type="date"
              value={formData.deadline}
              onChange={handleChange}
              min={getToday()}
              required
            />
          </div>
        </div>

        <div className="create-project-note">
          <span className="create-project-note-icon">ⓘ</span>
          <p>
            Your project will be submitted to the backend. Once saved,
            it should appear in the available projects list.
          </p>
        </div>

        <div className="create-project-actions">
          <button
            type="button"
            className="project-cancel-button"
            onClick={handleCancel}
            disabled={loading}
          >
            Cancel
          </button>

          <button
            type="submit"
            className="project-submit-button"
            disabled={loading}
          >
            {loading ? "Creating project..." : "Create Project"}
          </button>
        </div>
      </form>
    </div>
  );
}
