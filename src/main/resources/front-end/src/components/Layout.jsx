import React, { useEffect, useState } from "react";
import Navbar from "./NavBar";
import Footer from "./Footer";
import styles from "./styles/Layout.module.css";
import { getUserRole } from "../utils/auth";
import NavBarAdmin from "./NavBarAdmin";
import { useNavigate } from "react-router-dom";
import { loginPage } from "../App";
import NavBar from "./NavBar";

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
				navigate(loginPage);
			} finally {
				if (userRole === undefined) {
					navigate(loginPage);
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
	} else navigate(loginPage);
};

export default Layout;
