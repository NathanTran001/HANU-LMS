import React from "react";
import styles from "./styles/NavItem.module.css";

const NavItem = ({ path, label, isActive, onNavigate }) => {
	return (
		<div className={styles.navItem}>
			<a
				href={path}
				className={`${styles.navbarItem} ${isActive ? styles.active : ""}`}
				onClick={(e) => {
					e.preventDefault();
					onNavigate(path);
				}}
			>
				{label}
			</a>
		</div>
	);
};

export default NavItem;
