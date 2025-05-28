import React, { useEffect, useState } from "react";
import Footer from "./Footer";
import styles from "./styles/Layout.module.css";
import { getUserRole } from "../utils/auth";
import NavBarAdmin from "./NavBarAdmin";
import { useNavigate } from "react-router-dom";
import NavBar from "./NavBar";
import { LOGIN_PAGE } from "../constants/paths";

const Layout = ({ children }) => {
	const [userRole, setUserRole] = useState("");
	const [loading, setLoading] = useState(true);
	const navigate = useNavigate();

	useEffect(() => {
		const fetchUserRole = async () => {
			try {
				const role = await getUserRole();
				console.log(`User role fetched: ${role}`);

				setUserRole(role);
			} catch (error) {
				console.error("Error fetching user role:", error);
				navigate(LOGIN_PAGE);
			} finally {
				if (userRole === undefined) {
					navigate(LOGIN_PAGE);
				}
				setLoading(false);
			}
		};

		fetchUserRole();
	}, []);

	if (loading) {
		return (
			<div
				style={{
					display: "flex",
					justifyContent: "center",
					alignItems: "center",
					height: "100vh",
				}}
			>
				Loading...
			</div>
		);
	}

	// Use admin layout for admin users, regular layout for others
	if (userRole?.toLowerCase() === "admin") {
		return (
			<div className={styles.pageContainer}>
				<NavBarAdmin />
				<main className={styles.mainContent}>{children}</main>
				<Footer />
			</div>
		);
	} else if (
		userRole?.toLowerCase() === "student" ||
		userRole?.toLowerCase() === "lecturer"
	) {
		return (
			<div className={styles.pageContainer}>
				<NavBar />
				<main className={styles.mainContent}>{children}</main>
				<Footer />
			</div>
		);
	} else navigate(LOGIN_PAGE);
};

export default Layout;
