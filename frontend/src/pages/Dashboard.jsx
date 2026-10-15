
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axiosInstance from "../api/axiosInstance";
import "../styles/dashboard.css";
import ProjectList from "../components/ProjectList";

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

  const [userMessage, setUserMessage] = useState(
    "Verifying your session..."
  );
  const [userId, setUserId] = useState("");
  const [sessionError, setSessionError] = useState("");
  const [role, setRole] = useState(
    localStorage.getItem("dashboardPreviewRole") || "FREELANCER"
  );
  const [activePage, setActivePage] = useState("Overview");
  const [search, setSearch] = useState("");
  const [appliedProjects, setAppliedProjects] = useState([]);
  const [savedProjects, setSavedProjects] = useState([]);
  const [showNotifications, setShowNotifications] = useState(false);
  const [darkMode, setDarkMode] = useState(false);
  const [mobileMenu, setMobileMenu] = useState(false);
  const [notice, setNotice] = useState("");

  // Verify the saved JWT by calling the protected backend endpoint.
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
        if (match) {
          setUserId(match[1].trim());
        }

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

  function changeRole(nextRole) {
    setRole(nextRole);
    localStorage.setItem("dashboardPreviewRole", nextRole);
    setActivePage("Overview");
    setNotice(
      `Showing the ${nextRole.toLowerCase()} dashboard preview.`
    );
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
      current.includes(projectId)
        ? current
        : [...current, projectId]
    );

    setNotice(
      "Demo interaction only. Applications are not yet saved to the backend."
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

  return (
    <div className={`marketplace ${darkMode ? "dark-mode" : ""}`}>
      {mobileMenu && (
        <button
          className="mobile-overlay"
          aria-label="Close navigation"
          onClick={() => setMobileMenu(false)}
        />
      )}

      <aside
        className={`sidebar ${mobileMenu ? "sidebar-open" : ""}`}
      >
        <button
          className="brand"
          onClick={() => selectPage("Overview")}
          aria-label="FreelanceHub home"
        >
          <span className="brand-mark">F</span>
          <span>
            Freelance<span className="brand-accent">Hub</span>
          </span>
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
            <button
              onClick={() =>
                setNotice("Help center will be added soon.")
              }
            >
              Visit help center ↗
            </button>
          </div>

          <button
            className="sidebar-profile"
            onClick={() => selectPage("My Profile")}
          >
            <span className="avatar">S</span>
            <span className="profile-text">
              <strong>
                {userId ? `User ${userId}` : "Your account"}
              </strong>
              <small>
                {role === "CLIENT"
                  ? "Client preview"
                  : "Freelancer preview"}
              </small>
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
              Workspace <span>/</span>{" "}
              <strong>{activePage}</strong>
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
                onClick={() =>
                  setShowNotifications((current) => !current)
                }
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
            <div className="dashboard-alert">
              {sessionError}
            </div>
          )}

          {notice && (
            <div className="dashboard-notice">
              <span>{notice}</span>
              <button
                onClick={() => setNotice("")}
                aria-label="Dismiss message"
              >
                ×
              </button>
            </div>
          )}

          {/* OVERVIEW */}
          {activePage === "Overview" && (
            <>
              <section className="welcome-row">
                <div>
                  <div className="eyebrow">
                    <span className="eyebrow-dot" /> YOUR WORKSPACE
                  </div>

                  <h1>
                    Good to see you, there <span>✦</span>
                  </h1>

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
                    onChange={(event) =>
                      changeRole(event.target.value)
                    }
                    aria-label="Preview dashboard role"
                  >
                    <option value="FREELANCER">Freelancer</option>
                    <option value="CLIENT">Client</option>
                  </select>
                </div>
              </section>

              <section className="hero-banner">
                <div className="hero-copy">
                  <span className="hero-tag">
                    YOUR NEXT CHAPTER STARTS HERE
                  </span>

                  <h2>
                    {role === "FREELANCER" ? (
                      <>
                        Your skills deserve
                        <br />
                        the right opportunity.
                      </>
                    ) : (
                      <>
                        Great ideas start
                        <br />
                        with great people.
                      </>
                    )}
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
                        role === "FREELANCER"
                          ? "Explore Projects"
                          : "My Applications"
                      )
                    }
                  >
                    {role === "FREELANCER"
                      ? "Explore projects"
                      : "View workspace"}
                    <span>↗</span>
                  </button>
                </div>

                <div className="hero-art" aria-hidden="true">
                  <div className="art-circle circle-one" />
                  <div className="art-circle circle-two" />

                  <div className="art-window">
                    <div className="art-window-top">
                      <span />
                      <span />
                      <span />
                    </div>

                    <div className="art-code-line line-long" />
                    <div className="art-code-line line-medium" />
                    <div className="art-code-line line-short" />
                    <div className="art-code-line line-long" />
                    <div className="art-code-line line-medium" />
                    <div className="art-check">✓</div>
                  </div>

                  <div className="floating-badge">
                    ✦ Made for growth
                  </div>
                </div>
              </section>

              <section className="stats-grid">
                <article className="stat-card">
                  <div className="stat-top">
                    <span className="stat-icon stat-purple">⌕</span>
                    <span className="stat-label">OPPORTUNITIES</span>
                  </div>
                  <strong>Live</strong>
                  <p>Projects loaded from the backend</p>
                  <button
                    onClick={() => selectPage("Explore Projects")}
                  >
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
                  <button
                    onClick={() => selectPage("My Applications")}
                  >
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
                  <button
                    onClick={() => selectPage("Explore Projects")}
                  >
                    Find more <span>→</span>
                  </button>
                </article>
              </section>

              <section className="section-heading">
                <div>
                  <h2>
                    {role === "CLIENT"
                      ? "Projects to explore"
                      : "Recommended for you"}
                  </h2>
                  <p>
                    Projects are loaded from your marketplace backend.
                  </p>
                </div>

                <button
                  className="text-link"
                  onClick={() => selectPage("Explore Projects")}
                >
                  View all projects <span>→</span>
                </button>
              </section>

              <ProjectList search={search} />
            </>
          )}

          {/* EXPLORE PROJECTS */}
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

                <span className="result-count">Live projects</span>
              </section>

              <div className="filter-bar">
                <label className="filter-search">
                  <span>⌕</span>
                  <input
                    value={search}
                    onChange={(event) => setSearch(event.target.value)}
                    placeholder="Search title, skills, or category..."
                    aria-label="Search projects"
                  />
                </label>
              </div>

              <ProjectList search={search} />
            </>
          )}

          {/* MY APPLICATIONS */}
          {activePage === "My Applications" && (
            <>
              <section className="page-title-row">
                <div>
                  <div className="eyebrow">YOUR ACTIVITY</div>
                  <h1>
                    {role === "CLIENT"
                      ? "Hiring workspace"
                      : "My applications"}
                  </h1>
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
                  <p>
                    Connect your real project and application APIs to
                    review candidates here.
                  </p>
                  <button
                    className="primary-button"
                    onClick={() =>
                      setNotice(
                        "Project creation will be connected to your backend next."
                      )
                    }
                  >
                    Create a project
                  </button>
                </div>
              ) : appliedProjects.length === 0 ? (
                <div className="empty-state">
                  <span>▤</span>
                  <h3>No applications yet</h3>
                  <p>
                    Applications are still a demo feature and are not
                    saved to the backend.
                  </p>
                  <button
                    className="primary-button"
                    onClick={() => selectPage("Explore Projects")}
                  >
                    Explore projects
                  </button>
                </div>
              ) : (
                <div className="empty-state">
                  <span>▤</span>
                  <h3>Demo applications</h3>
                  <p>
                    Real applications will appear here after the
                    application API is connected.
                  </p>
                </div>
              )}
            </>
          )}

          {/* CONTRACTS */}
          {activePage === "Contracts" && (
            <PlaceholderPage
              icon="▣"
              title="Your contracts"
              description="Active contracts, milestones, payments, and completion status will appear here once connected to your backend APIs."
              button="Explore projects"
              onAction={() => selectPage("Explore Projects")}
            />
          )}

          {/* MESSAGES */}
          {activePage === "Messages" && (
            <PlaceholderPage
              icon="✉"
              title="Messages"
              description="Keep client and freelancer conversations organized. Messaging can be added when the backend supports it."
              button="Back to overview"
              onAction={() => selectPage("Overview")}
            />
          )}

          {/* PROFILE */}
          {activePage === "My Profile" && (
            <>
              <section className="page-title-row">
                <div>
                  <div className="eyebrow">YOUR ACCOUNT</div>
                  <h1>My profile</h1>
                  <p className="page-subtitle">
                    Your authenticated session details.
                  </p>
                </div>
              </section>

              <section className="profile-card">
                <div className="profile-large-avatar">S</div>

                <div className="profile-info">
                  <h2>
                    {userId
                      ? `User ${userId}`
                      : "FreelanceHub member"}
                  </h2>

                  <p>{userMessage}</p>
                  <span className="status-pill">Authenticated</span>

                  <div className="profile-role-row">
                    <span>Dashboard preview</span>
                    <strong>
                      {role === "CLIENT" ? "Client" : "Freelancer"}
                    </strong>
                  </div>

                  <p className="profile-hint">
                    This role selector is only a UI preview.
                    Authorization is controlled by the backend.
                  </p>
                </div>
              </section>
            </>
          )}

          <footer className="dashboard-footer">
            <span>© 2026 FreelanceHub</span>
            <span>
              Built to connect talent with opportunity <b>✦</b>
            </span>
            <button onClick={handleLogout}>Sign out ↗</button>
          </footer>
        </div>
      </main>
    </div>
  );
}

function PlaceholderPage({
  icon,
  title,
  description,
  button,
  onAction,
}) {
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
        <button className="primary-button" onClick={onAction}>
          {button}
        </button>
      </div>
    </>
  );
}
