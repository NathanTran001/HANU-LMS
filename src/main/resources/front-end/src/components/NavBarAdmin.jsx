// components/NavBarAdmin.js
import React from "react";
import { useLocation } from "react-router-dom";
import { useNavBar } from "../hooks/useNavBar";
import styles from "./styles/NavBar.module.css";

const NavBarAdmin = () => {
	const {
		isDropdownOpen,
		user,
		dropdownRef,
		navigate,
		toggleDropdown,
		handleLogout,
	} = useNavBar();

	const location = useLocation();

	const isActiveRoute = (path) => {
		return location.pathname === path;
	};

	const adminNavItems = [
		{ path: "/admin/listLecturer", label: "Lecturer" },
		{ path: "/admin/listStudent", label: "Student" },
		{ path: "/admin/listFaculty", label: "Faculty" },
	];

	return (
		<div className={styles.containerFluid}>
			<div
				className={styles.row}
				id={styles.bodyContainer}
			>
				<div className={styles.leftSection}>
					<div className={styles.logoSection}>
						<div className={styles.logo}>
							<img
								src="/images/HANU.png"
								alt="HANU Logo"
								className={styles.logoImage}
							/>
							<span className={styles.logoTitle}>HANU LMS</span>
						</div>
					</div>
					<div className={styles.navSection}>
						<div className={styles.navItems}>
							{adminNavItems.map((item) => (
								<div
									key={item.path}
									className={styles.navItem}
								>
									<a
										href={item.path}
										className={`${styles.navbarItem} ${
											isActiveRoute(item.path) ? styles.active : ""
										}`}
										onClick={(e) => {
											e.preventDefault();
											navigate(item.path);
										}}
									>
										{item.label}
									</a>
								</div>
							))}
						</div>
					</div>
				</div>

				<div className={styles.rightSection}>
					<div className={styles.userControls}>
						<div className={styles.userSection}>
							<span className={styles.userFullName}>
								{user?.name || user?.username || "Admin"}
							</span>

							<div className={styles.avatarContainer}>
								<img
									src="/images/HANU.png"
									alt="Admin Avatar"
								/>
							</div>

							<button
								className={styles.button}
								onClick={toggleDropdown}
								ref={dropdownRef}
							>
								<i
									className="bi bi-chevron-down"
									id={styles.chevronIcon}
								></i>
							</button>
						</div>

						<div
							className={`${styles.dropdownContent} ${
								isDropdownOpen ? styles.show : ""
							}`}
						>
							<a
								className={styles.hyperlink}
								href="#"
								onClick={(e) => {
									e.preventDefault();
									handleLogout();
								}}
							>
								<i className="bi bi-box-arrow-right"></i> Logout
							</a>
						</div>
					</div>
				</div>
			</div>
		</div>
	);
};

export default NavBarAdmin;
