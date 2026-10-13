
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axiosInstance from "../api/AxiosInstance";
import "../styles/auth.css";

export default function Dashboard() {
  const navigate = useNavigate();
  const [message, setMessage] = useState("Verifying your session...");
  const [error, setError] = useState(false);

  useEffect(() => {
    let active = true;

    async function verifyToken() {
      if (!localStorage.getItem("token")) {
        navigate("/login", { replace: true });
        return;
      }

      try {
        const response = await axiosInstance.get("/test");

        if (active) {
          setMessage(response.data);
          setError(false);
        }
      } catch (err) {
        if (active) {
          setError(true);
          setMessage(
            err.response?.data?.message ||
              "Session verification failed. Please sign in again."
          );

          if (err.response?.status === 401 ||
              err.response?.status === 403) {
            localStorage.removeItem("token");
            localStorage.removeItem("tokenType");
            navigate("/login", { replace: true });
          }
        }
      }
    }

    verifyToken();

    return () => {
      active = false;
    };
  }, [navigate]);

  function handleLogout() {
    localStorage.removeItem("token");
    localStorage.removeItem("tokenType");
    navigate("/login", { replace: true });
  }

  return (
    <main className="auth-page">
      <section className="auth-card">
        <div className="auth-brand">FreelanceHub</div>
        <h1>Dashboard</h1>
        <p className="auth-subtitle">
          Your frontend is connected to your marketplace backend.
        </p>

        <div className={error ? "auth-error" : "auth-success"}>
          {message}
        </div>

        <button onClick={handleLogout}>Sign out</button>
      </section>
    </main>
  );
}

