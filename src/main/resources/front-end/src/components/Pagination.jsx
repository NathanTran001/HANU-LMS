import React from "react";
import styles from "./styles/Pagination.module.css";

const Pagination = ({ page, totalPages, onPageChange }) => (
	<div className={styles.pageIndicesContainer}>
		<button
			onClick={() => onPageChange(page - 1)}
			disabled={page === 0}
			className={styles.paginationButton}
		>
			<i className="bi bi-chevron-left"></i>
		</button>
		<span className={styles.pageInfo}>
			Page {page + 1} of {totalPages}
		</span>
		<button
			onClick={() => onPageChange(page + 1)}
			disabled={page >= totalPages - 1}
			className={styles.paginationButton}
		>
			<i className="bi bi-chevron-right"></i>
		</button>
	</div>
);

export default Pagination;
