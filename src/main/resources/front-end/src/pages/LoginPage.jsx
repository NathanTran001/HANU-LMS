import React, { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import styles from "./styles/LoginPage.module.css";
import api from "../services/apiService";
import { getUserRole } from "../utils/auth";
import {
	FACULTY_LIST_PAGE,
	LECTURER_LIST_PAGE,
	MY_COURSES_PAGE,
} from "../constants/paths";
import { ADMIN, LECTURER, STUDENT } from "../constants/roles";

const LoginPage = () => {
	const [credentials, setCredentials] = useState({
		username: "",
		password: "",
	});
	const [error, setError] = useState("");
	const [message, setMessage] = useState("");
	const [isLoading, setIsLoading] = useState(false);

	const navigate = useNavigate();
	const location = useLocation();

	useEffect(() => {
		const urlParams = new URLSearchParams(location.search);
		if (urlParams.get("logout") !== null) {
			setMessage("You have been logged out");
		}
		if (urlParams.get("error") !== null) {
			setError("Bad Credentials!");
		}
		handleRedirect();
	}, [location]);

	const handleInputChange = (e) => {
		const { name, value } = e.target;
		setCredentials((prev) => ({
			...prev,
			[name]: value,
		}));
		if (error) setError("");
	};

	const handleLogin = async (e) => {
		e.preventDefault();
		setIsLoading(true);
		setError("");

		try {
			await api.login(credentials);
			handleRedirect();
		} catch (err) {
			if (err.response) {
				setError(err.response.data.message);
			} else {
				console.error("Login error:", err);
				setError("Network error. Please try again.");
			}
		} finally {
			setIsLoading(false);
		}
	};

	const handleCancel = () => {
		navigate("/");
	};

	const handleSignupRedirect = () => {
		navigate("/signup");
	};

	const handleRedirect = async () => {
		const role = await getUserRole();
		if (!role) return;
		console.log(`Role: ${role}`);
		if (role.includes(ADMIN)) {
			navigate(LECTURER_LIST_PAGE);
			console.log(role);
		} else if (role.includes(LECTURER) || role.includes(STUDENT))
			navigate(MY_COURSES_PAGE);
	};

	return (
		<div className={styles.background}>
			<form
				className={styles.form}
				onSubmit={handleLogin}
			>
				<h3 className={styles.title}>Login</h3>

				<label
					htmlFor="username"
					className={styles.label}
				>
					Username
				</label>
				<input
					type="text"
					placeholder="Enter username"
					id="username"
					name="username"
					className={styles.input}
					value={credentials.username}
					onChange={handleInputChange}
					required
				/>

				<label
					htmlFor="password"
					className={styles.label}
				>
					Password
				</label>
				<input
					type="password"
					placeholder="Enter password"
					id="password"
					name="password"
					className={styles.input}
					value={credentials.password}
					onChange={handleInputChange}
					required
				/>

				{error && (
					<div>
						<p className={styles.textError}>{error}</p>
					</div>
				)}

				{message && (
					<div>
						<p className={styles.textInfo}>{message}</p>
					</div>
				)}

				<a
					href="#"
					className={styles.link}
					onClick={(e) => {
						e.preventDefault();
						handleSignupRedirect();
					}}
				>
					Don't have an ADMIN account? Signup
				</a>

				<div className={styles.buttons}>
					<button
						type="button"
						className={`${styles.button} ${styles.transparentButton}`}
						onClick={handleCancel}
						disabled={isLoading}
					>
						Cancel
					</button>
					<button
						type="submit"
						className={`${styles.button} ${styles.redButton}`}
						disabled={isLoading}
					>
						{isLoading ? "Logging in..." : "Login"}
					</button>
				</div>
			</form>
		</div>
	);
};

export default LoginPage;
