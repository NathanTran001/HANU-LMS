import React from "react";
import { NavLink } from "react-router-dom";
import { useNavBar } from "../hooks/useNavBar";
import { useSearch } from "../hooks/useSearch";
import styles from "./styles/NavBar.module.css";
import logo from "../assets/HANU.png";

const NavBar = () => {
	const {
		isDropdownOpen,
		user,
		dropdownRef,
		navigate,
		toggleDropdown,
		handleLogout,
	} = useNavBar();

	const {
		searchValue,
		setSearchValue,
		handleSearchFocus,
		handleSearchBlur,
		handleSearchKeyPress,
	} = useSearch();

	const userNavItems = [
		{ path: "/", label: "Home" },
		{ path: "/myCourses", label: "My Courses" },
		{ path: "/facultyAnnouncement", label: "Faculty Announcements" },
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
								src={logo}
								alt="HANU Logo"
								className={styles.logoImage}
							/>
							<span className={styles.logoTitle}>HANU LMS</span>
						</div>
					</div>
					<div className={styles.navSection}>
						<div className={styles.navItems}>
							{userNavItems.map((item) => (
								<NavLink
									key={item.path}
									to={item.path}
									className={({ isActive }) =>
										`${styles.navbarItem} ${isActive ? styles.active : ""}`
									}
								>
									{item.label}
								</NavLink>
							))}
						</div>
					</div>
				</div>

				<div className={styles.rightSection}>
					<div className={styles.userControls}>
						<div className={styles.searchbarContainer}>
							<i
								className="bi bi-search"
								aria-hidden="true"
								id={styles.searchIcon}
							></i>
							<input
								type="text"
								value={searchValue}
								className={styles.searchbar}
								onChange={(e) => setSearchValue(e.target.value)}
								onFocus={handleSearchFocus}
								onBlur={handleSearchBlur}
								onKeyDown={handleSearchKeyPress}
							/>
						</div>

						<div className={styles.userSection}>
							<button className={styles.button}>
								<i className="bi bi-bell-fill"></i>
							</button>

							<span className={styles.userFullName}>
								{user?.name || user?.username || "User"}
							</span>

							<div className={styles.avatarContainer}>
								<img
									src="/images/HANU.png"
									alt="User Avatar"
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

export default NavBar;
