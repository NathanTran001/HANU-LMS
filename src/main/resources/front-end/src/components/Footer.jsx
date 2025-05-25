import React from "react";
import styles from "./styles/Footer.module.css";

const Footer = () => {
	const currentYear = new Date().getFullYear();

	return (
		<div className={styles.footerContainer}>
			&copy; {currentYear} NathanTran, Inc
		</div>
	);
};

export default Footer;
