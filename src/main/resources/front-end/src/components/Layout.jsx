import React, { useEffect, useState } from "react";
import Footer from "./Footer";
import styles from "./styles/Layout.module.css";
import { getUserRole } from "../utils/auth";
import NavBarAdmin from "./NavBarAdmin";
import { Navigate, Outlet, useNavigate } from "react-router-dom";
import NavBar from "./NavBar";
import { LOGIN_PAGE } from "../constants/paths";
import { ADMIN, LECTURER, STUDENT } from "../constants/roles";
import { useAuth } from "../contexts/AuthContext";

const Layout = () => {
	const { user, loading } = useAuth();

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
	if (user?.roles?.includes(ADMIN)) {
		return (
			<div className={styles.pageContainer}>
				<NavBarAdmin />
				<main className={styles.mainContent}>
					<Outlet />
				</main>
				<Footer />
			</div>
		);
	} else if (
		user?.roles?.includes(STUDENT) ||
		user?.roles?.includes(LECTURER)
	) {
		return (
			<div className={styles.pageContainer}>
				<NavBar />
				<main className={styles.mainContent}>
					<Outlet />
				</main>
				<Footer />
			</div>
		);
	} else {
		return (
			<Navigate
				to={LOGIN_PAGE}
				replace
			/>
		);
	}
};

export default Layout;
