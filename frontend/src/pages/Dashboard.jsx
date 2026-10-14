
import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import axiosInstance from "../api/axiosInstance";
import "../styles/dashboard.css";

const sampleProjects = [
  {
    id: 101,
    title: "Build a responsive business website",
    company: "Bright Studio",
    description:
      "Create a modern, mobile-friendly website with reusable components and a clean layout.",
    skills: ["React", "CSS", "JavaScript"],
    budget: "₹8,000–₹15,000",
    duration: "2–3 weeks",
    category: "Frontend",
    icon: "🎨",
    color: "purple",
  },
  {
    id: 102,
    title: "Develop a REST API for an application",
    company: "CloudNova",
    description:
      "Build secure REST endpoints, connect a relational database, and document the APIs.",
    skills: ["Java", "Spring Boot", "MySQL"],
    budget: "₹12,000–₹25,000",
    duration: "3–4 weeks",
    category: "Backend",
    icon: "⚙️",
    color: "blue",
  },
  {
    id: 103,
    title: "Create a full-stack task manager",
    company: "Pixel Works",
    description:
      "Develop a task management application with authentication and a simple dashboard.",
    skills: ["React", "Java", "SQL"],
    budget: "₹15,000–₹30,000",
    duration: "4–6 weeks",
    category: "Full Stack",
    icon: "🚀",
    color: "orange",
  },
  {
    id: 104,
    title: "Design a clean product landing page",
    company: "Launch Labs",
    description:
      "Turn a product concept into an attractive landing page with responsive sections.",
    skills: ["HTML", "CSS", "JavaScript"],
    budget: "₹5,000–₹10,000",
    duration: "1–2 weeks",
    category: "Frontend",
    icon: "✨",
    color: "pink",
  },
];

const navigation = [
  { label: "Overview", icon: "◫" },
  { label: "Explore Projects", icon: "⌕" },
  { label: "My Applications", icon: "▤" },
  { label: "Contracts", icon: "▣" },
  { label: "Messages", icon: "✉" },
  { label: "My Profile", icon: "◎" },
];

export default function Dashboard() {
  const navigate = useNavigate();

  const [userMessage, setUserMessage] = useState("Verifying your session...");
  const [userId, setUserId] = useState("");
  const [sessionError, setSessionError] = useState("");
  const [role, setRole] = useState(
    localStorage.getItem("dashboardPreviewRole") || "FREELANCER"
  );
  const [activePage, setActivePage] = useState("Overview");
  const [search, setSearch] = useState("");
  const [category, setCategory] = useState("All");
  const [appliedProjects, setAppliedProjects] = useState([]);
  const [savedProjects, setSavedProjects] = useState([]);
  const [showNotifications, setShowNotifications] = useState(false);
  const [darkMode, setDarkMode] = useState(false);
  const [mobileMenu, setMobileMenu] = useState(false);
  const [notice, setNotice] = useState("");

  useEffect(() => {
    let active = true;

    async function verifySession() {
      if (!localStorage.getItem("token")) {
        navigate("/login", { replace: true });
        return;
      }

      try {
        const response = await axiosInstance.get("/test");

        if (!active) return;

        const message = String(response.data);
        setUserMessage(message);

        const match = message.match(/user ID:\s*(.+)$/i);
        if (match) setUserId(match[1].trim());

        setSessionError("");
      } catch (error) {
        if (!active) return;

        const status = error.response?.status;

        if (status === 401 || status === 403) {
          localStorage.removeItem("token");
          localStorage.removeItem("tokenType");
          navigate("/login", { replace: true });
          return;
        }

        setSessionError(
          "We couldn't verify your session. Check that your backend is running."
        );
      }
    }

    verifySession();

    return () => {
      active = false;
    };
  }, [navigate]);

  const filteredProjects = useMemo(() => {
    return sampleProjects.filter((project) => {
      const text = [
        project.title,
        project.company,
        project.description,
        project.category,
        ...project.skills,
      ]
        .join(" ")
        .toLowerCase();

      const matchesSearch = text.includes(search.toLowerCase());
      const matchesCategory =
        category === "All" || project.category === category;

      return matchesSearch && matchesCategory;
    });
  }, [search, category]);

  function changeRole(nextRole) {
    setRole(nextRole);
    localStorage.setItem("dashboardPreviewRole", nextRole);
    setActivePage("Overview");
    setNotice(`Showing the ${nextRole.toLowerCase()} dashboard preview.`);
  }

  function toggleSaved(projectId) {
    setSavedProjects((current) =>
      current.includes(projectId)
        ? current.filter((id) => id !== projectId)
        : [...current, projectId]
    );
  }

  function applyToProject(projectId) {
    setAppliedProjects((current) =>
      current.includes(projectId) ? current : [...current, projectId]
    );
    setNotice(
      "Demo interaction only. Project applications will connect to the backend next."
    );
  }

  function handleLogout() {
    localStorage.removeItem("token");
    localStorage.removeItem("tokenType");
    navigate("/login", { replace: true });
  }

  function selectPage(page) {
    setActivePage(page);
    setMobileMenu(false);
    setNotice("");
  }

  const firstName = "there";

  return (
    <div className={`marketplace ${darkMode ? "dark-mode" : ""}`}>
      {mobileMenu && (
        <button
          className="mobile-overlay"
          aria-label="Close navigation"
          onClick={() => setMobileMenu(false)}
        />
      )}

      <aside className={`sidebar ${mobileMenu ? "sidebar-open" : ""}`}>
        <button
          className="brand"
          onClick={() => selectPage("Overview")}
          aria-label="FreelanceHub home"
        >
          <span className="brand-mark">F</span>
          <span>Freelance<span className="brand-accent">Hub</span></span>
        </button>

        <div className="workspace-label">WORKSPACE</div>

        <nav className="side-nav" aria-label="Main navigation">
          {navigation.map((item) => (
            <button
              key={item.label}
              className={`nav-item ${
                activePage === item.label ? "nav-active" : ""
              }`}
              onClick={() => selectPage(item.label)}
            >
              <span className="nav-icon">{item.icon}</span>
              <span>{item.label}</span>
              {item.label === "Messages" && (
                <span className="nav-count">2</span>
              )}
            </button>
          ))}
        </nav>

        <div className="sidebar-bottom">
          <div className="help-card">
            <div className="help-icon">✦</div>
            <strong>Need a hand?</strong>
            <p>Make the most of your workspace.</p>
            <button onClick={() => setNotice("Help center will be added soon.")}>
              Visit help center ↗
            </button>
          </div>

          <button className="sidebar-profile" onClick={() => selectPage("My Profile")}>
            <span className="avatar">S</span>
            <span className="profile-text">
              <strong>{userId ? `User ${userId}` : "Your account"}</strong>
              <small>{role === "CLIENT" ? "Client preview" : "Freelancer preview"}</small>
            </span>
            <span className="profile-menu">···</span>
          </button>
        </div>
      </aside>

      <main className="main-area">
        <header className="topbar">
          <div className="topbar-left">
            <button
              className="mobile-menu-button"
              onClick={() => setMobileMenu(true)}
              aria-label="Open navigation"
            >
              ☰
            </button>
            <div className="breadcrumb">
              Workspace <span>/</span> <strong>{activePage}</strong>
            </div>
          </div>

          <div className="topbar-actions">
            <label className="top-search">
              <span>⌕</span>
              <input
                value={search}
                onChange={(event) => {
                  setSearch(event.target.value);
                  setActivePage("Explore Projects");
                }}
                placeholder="Search projects..."
                aria-label="Search projects"
              />
              <kbd>⌘ K</kbd>
            </label>

            <button
              className="icon-button"
              title="Toggle dark mode"
              aria-label="Toggle dark mode"
              onClick={() => setDarkMode((current) => !current)}
            >
              {darkMode ? "☀" : "☾"}
            </button>

            <div className="notification-wrapper">
              <button
                className="icon-button notification-button"
                title="Notifications"
                aria-label="Toggle notifications"
                onClick={() => setShowNotifications((current) => !current)}
              >
                ♧
                <span className="notification-dot" />
              </button>

              {showNotifications && (
                <div className="notification-popover">
                  <div className="popover-heading">
                    <strong>Notifications</strong>
                    <span>Demo</span>
                  </div>
                  <div className="notification-item">
                    <span className="notification-symbol">✦</span>
                    <div>
                      <strong>Welcome to FreelanceHub</strong>
                      <p>Your workspace is ready to explore.</p>
                    </div>
                  </div>
                  <div className="notification-item">
                    <span className="notification-symbol">↗</span>
                    <div>
                      <strong>Keep building your profile</strong>
                      <p>A complete profile helps you get noticed.</p>
                    </div>
                  </div>
                </div>
              )}
            </div>

            <button
              className="top-avatar"
              onClick={() => selectPage("My Profile")}
              aria-label="Open profile"
            >
              S
            </button>
          </div>
        </header>

        <div className="page-content">
          {sessionError && (
            <div className="dashboard-alert">{sessionError}</div>
          )}

          {notice && (
            <div className="dashboard-notice">
              <span>{notice}</span>
              <button onClick={() => setNotice("")} aria-label="Dismiss message">
                ×
              </button>
            </div>
          )}

          {activePage === "Overview" && (
            <>
              <section className="welcome-row">
                <div>
                  <div className="eyebrow">
                    <span className="eyebrow-dot" /> YOUR WORKSPACE
                  </div>
                  <h1>Good to see you, {firstName} <span>✦</span></h1>
                  <p className="page-subtitle">
                    {role === "FREELANCER"
                      ? "Discover opportunities and take your next step."
                      : "Find talented people and bring your ideas to life."}
                  </p>
                </div>

                <div className="role-switch">
                  <span>Preview as</span>
                  <select
                    value={role}
                    onChange={(event) => changeRole(event.target.value)}
                    aria-label="Preview dashboard role"
                  >
                    <option value="FREELANCER">Freelancer</option>
                    <option value="CLIENT">Client</option>
                  </select>
                </div>
              </section>

              <section className="hero-banner">
                <div className="hero-copy">
                  <span className="hero-tag">YOUR NEXT CHAPTER STARTS HERE</span>
                  <h2>
                    {role === "FREELANCER"
                      ? <>Your skills deserve<br />the right opportunity.</>
                      : <>Great ideas start<br />with great people.</>}
                  </h2>
                  <p>
                    {role === "FREELANCER"
                      ? "Explore projects, showcase your skills, and build your freelance journey."
                      : "Discover skilled freelancers and turn your next idea into reality."}
                  </p>
                  <button
                    className="hero-button"
                    onClick={() =>
                      selectPage(
                        role === "FREELANCER" ? "Explore Projects" : "My Applications"
                      )
                    }
                  >
                    {role === "FREELANCER" ? "Explore projects" : "View workspace"}
                    <span>↗</span>
                  </button>
                </div>
                <div className="hero-art" aria-hidden="true">
                  <div className="art-circle circle-one" />
                  <div className="art-circle circle-two" />
                  <div className="art-window">
                    <div className="art-window-top">
                      <span /><span /><span />
                    </div>
                    <div className="art-code-line line-long" />
                    <div className="art-code-line line-medium" />
                    <div className="art-code-line line-short" />
                    <div className="art-code-line line-long" />
                    <div className="art-code-line line-medium" />
                    <div className="art-check">✓</div>
                  </div>
                  <div className="floating-badge">✦ Made for growth</div>
                </div>
              </section>

              <section className="stats-grid">
                <article className="stat-card">
                  <div className="stat-top">
                    <span className="stat-icon stat-purple">⌕</span>
                    <span className="stat-label">OPPORTUNITIES</span>
                  </div>
                  <strong>{sampleProjects.length}</strong>
                  <p>Sample projects to explore</p>
                  <button onClick={() => selectPage("Explore Projects")}>
                    Browse projects <span>→</span>
                  </button>
                </article>

                <article className="stat-card">
                  <div className="stat-top">
                    <span className="stat-icon stat-green">✓</span>
                    <span className="stat-label">APPLICATIONS</span>
                  </div>
                  <strong>{appliedProjects.length}</strong>
                  <p>Demo applications submitted</p>
                  <button onClick={() => selectPage("My Applications")}>
                    View applications <span>→</span>
                  </button>
                </article>

                <article className="stat-card">
                  <div className="stat-top">
                    <span className="stat-icon stat-orange">♡</span>
                    <span className="stat-label">SAVED PROJECTS</span>
                  </div>
                  <strong>{savedProjects.length}</strong>
                  <p>Projects in your saved list</p>
                  <button onClick={() => selectPage("Explore Projects")}>
                    Find more <span>→</span>
                  </button>
                </article>
              </section>

              <section className="section-heading">
                <div>
                  <h2>{role === "CLIENT" ? "Projects to explore" : "Recommended for you"}</h2>
                  <p>Explore a few opportunities from our sample listings.</p>
                </div>
                <button className="text-link" onClick={() => selectPage("Explore Projects")}>
                  View all projects <span>→</span>
                </button>
              </section>

              <ProjectGrid
                projects={filteredProjects.slice(0, 3)}
                role={role}
                appliedProjects={appliedProjects}
                savedProjects={savedProjects}
                onApply={applyToProject}
                onSave={toggleSaved}
              />
            </>
          )}

          {activePage === "Explore Projects" && (
            <>
              <section className="page-title-row">
                <div>
                  <div className="eyebrow">OPPORTUNITIES</div>
                  <h1>Explore projects</h1>
                  <p className="page-subtitle">
                    Find projects that match your interests and skills.
                  </p>
                </div>
                <span className="result-count">
                  {filteredProjects.length} results
                </span>
              </section>

              <div className="filter-bar">
                <label className="filter-search">
                  <span>⌕</span>
                  <input
                    value={search}
                    onChange={(event) => setSearch(event.target.value)}
                    placeholder="Search title, skills, or category..."
                  />
                </label>
                <select
                  value={category}
                  onChange={(event) => setCategory(event.target.value)}
                  aria-label="Filter project category"
                >
                  <option>All</option>
                  <option>Frontend</option>
                  <option>Backend</option>
                  <option>Full Stack</option>
                </select>
              </div>

              {filteredProjects.length ? (
                <ProjectGrid
                  projects={filteredProjects}
                  role={role}
                  appliedProjects={appliedProjects}
                  savedProjects={savedProjects}
                  onApply={applyToProject}
                  onSave={toggleSaved}
                />
              ) : (
                <div className="empty-state">
                  <span>⌕</span>
                  <h3>No matching projects</h3>
                  <p>Try a different keyword or clear your category filter.</p>
                  <button
                    className="primary-button"
                    onClick={() => {
                      setSearch("");
                      setCategory("All");
                    }}
                  >
                    Clear filters
                  </button>
                </div>
              )}
            </>
          )}

          {activePage === "My Applications" && (
            <>
              <section className="page-title-row">
                <div>
                  <div className="eyebrow">YOUR ACTIVITY</div>
                  <h1>{role === "CLIENT" ? "Hiring workspace" : "My applications"}</h1>
                  <p className="page-subtitle">
                    {role === "CLIENT"
                      ? "Your client-side workspace preview."
                      : "Keep track of the projects you have applied to."}
                  </p>
                </div>
              </section>

              {role === "CLIENT" ? (
                <div className="empty-state">
                  <span>▤</span>
                  <h3>Your hiring workspace</h3>
                  <p>Connect your real project and application APIs to review candidates here.</p>
                  <button className="primary-button" onClick={() => setNotice("Project creation will be connected to your backend next.")}>
                    Create a project
                  </button>
                </div>
              ) : appliedProjects.length === 0 ? (
                <div className="empty-state">
                  <span>▤</span>
                  <h3>No applications yet</h3>
                  <p>Explore projects and try the Apply button to preview this page.</p>
                  <button className="primary-button" onClick={() => selectPage("Explore Projects")}>
                    Explore projects
                  </button>
                </div>
              ) : (
                <div className="application-list">
                  {sampleProjects
                    .filter((project) => appliedProjects.includes(project.id))
                    .map((project) => (
                      <article className="application-row" key={project.id}>
                        <div className={`project-icon ${project.color}`}>{project.icon}</div>
                        <div className="application-details">
                          <h3>{project.title}</h3>
                          <p>{project.company} · {project.budget}</p>
                        </div>
                        <span className="status-pill">Demo submitted</span>
                      </article>
                    ))}
                </div>
              )}
            </>
          )}

          {activePage === "Contracts" && (
            <PlaceholderPage
              icon="▣"
              title="Your contracts"
              description="Active contracts, milestones, payments, and completion status will appear here once connected to your backend APIs."
              button="Explore projects"
              onAction={() => selectPage("Explore Projects")}
            />
          )}

          {activePage === "Messages" && (
            <PlaceholderPage
              icon="✉"
              title="Messages"
              description="Keep client and freelancer conversations organized. Messaging can be added when the backend supports it."
              button="Back to overview"
              onAction={() => selectPage("Overview")}
            />
          )}

          {activePage === "My Profile" && (
            <>
              <section className="page-title-row">
                <div>
                  <div className="eyebrow">YOUR ACCOUNT</div>
                  <h1>My profile</h1>
                  <p className="page-subtitle">Your authenticated session details.</p>
                </div>
              </section>
              <section className="profile-card">
                <div className="profile-large-avatar">S</div>
                <div className="profile-info">
                  <h2>{userId ? `User ${userId}` : "FreelanceHub member"}</h2>
                  <p>{userMessage}</p>
                  <span className="status-pill">Authenticated</span>
                  <div className="profile-role-row">
                    <span>Dashboard preview</span>
                    <strong>{role === "CLIENT" ? "Client" : "Freelancer"}</strong>
                  </div>
                  <p className="profile-hint">
                    Your actual account name, email, and role should come from a backend profile endpoint.
                  </p>
                </div>
              </section>
            </>
          )}

          <footer className="dashboard-footer">
            <span>© 2026 FreelanceHub</span>
            <span>Built to connect talent with opportunity <b>✦</b></span>
            <button onClick={handleLogout}>Sign out ↗</button>
          </footer>
        </div>
      </main>
    </div>
  );
}

function ProjectGrid({
  projects,
  role,
  appliedProjects,
  savedProjects,
  onApply,
  onSave,
}) {
  return (
    <div className="project-grid">
      {projects.map((project) => {
        const applied = appliedProjects.includes(project.id);
        const saved = savedProjects.includes(project.id);

        return (
          <article className="project-card" key={project.id}>
            <div className="project-card-top">
              <div className={`project-icon ${project.color}`}>{project.icon}</div>
              <button
                className={`save-button ${saved ? "is-saved" : ""}`}
                onClick={() => onSave(project.id)}
                aria-label={saved ? "Remove saved project" : "Save project"}
                title={saved ? "Remove saved project" : "Save project"}
              >
                {saved ? "♥" : "♡"}
              </button>
            </div>

            <div className="company-line">
              <span className="company-dot" />
              {project.company}
              <span className="demo-tag">SAMPLE</span>
            </div>

            <h3>{project.title}</h3>
            <p className="project-description">{project.description}</p>

            <div className="skill-list">
              {project.skills.map((skill) => (
                <span key={skill}>{skill}</span>
              ))}
            </div>

            <div className="project-meta">
              <div>
                <small>PROJECT BUDGET</small>
                <strong>{project.budget}</strong>
              </div>
              <div>
                <small>EST. DURATION</small>
                <strong>{project.duration}</strong>
              </div>
            </div>

            <div className="project-card-bottom">
              <span className="category-label">{project.category}</span>
              {role === "FREELANCER" ? (
                <button
                  className={`apply-button ${applied ? "applied" : ""}`}
                  disabled={applied}
                  onClick={() => onApply(project.id)}
                >
                  {applied ? "Applied ✓" : "Apply now →"}
                </button>
              ) : (
                <button
                  className="apply-button"
                  onClick={() => onApply(project.id)}
                >
                  {applied ? "Selected ✓" : "View project →"}
                </button>
              )}
            </div>
          </article>
        );
      })}
    </div>
  );
}

function PlaceholderPage({ icon, title, description, button, onAction }) {
  return (
    <>
      <section className="page-title-row">
        <div>
          <div className="eyebrow">WORKSPACE</div>
          <h1>{title}</h1>
          <p className="page-subtitle">{description}</p>
        </div>
      </section>
      <div className="empty-state">
        <span>{icon}</span>
        <h3>{title} are on the way</h3>
        <p>{description}</p>
        <button className="primary-button" onClick={onAction}>{button}</button>
      </div>
    </>
  );
}
