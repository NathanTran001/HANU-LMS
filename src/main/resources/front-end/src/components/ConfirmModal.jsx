import React, { useEffect } from "react";
import styles from "./styles/ConfirmModal.module.css";

const ConfirmModal = ({
	isOpen,
	title,
	message,
	onConfirm,
	onCancel,
	confirmText = "Confirm",
	cancelText = "Cancel",
	confirmButtonClass = "",
	cancelButtonClass = "",
}) => {
	// Handle ESC key press
	useEffect(() => {
		const handleEscKey = (event) => {
			if (event.key === "Escape" && isOpen) {
				onCancel();
			}
		};

		if (isOpen) {
			document.addEventListener("keydown", handleEscKey);
			// Prevent body scroll when modal is open
			document.body.style.overflow = "hidden";
		}

		return () => {
			document.removeEventListener("keydown", handleEscKey);
			document.body.style.overflow = "unset";
		};
	}, [isOpen, onCancel]);

	const handleBackdropClick = (e) => {
		if (e.target === e.currentTarget) {
			onCancel();
		}
	};

	if (!isOpen) return null;

	return (
		<div
			className={styles.modalOverlay}
			onClick={handleBackdropClick}
		>
			<div className={styles.modalDialog}>
				<div className={styles.modalContent}>
					<div className={styles.modalHeader}>
						<h5 className={styles.modalTitle}>{title}</h5>
						<button
							type="button"
							className={styles.closeButton}
							onClick={onCancel}
							aria-label="Close"
						>
							<span aria-hidden="true">&times;</span>
						</button>
					</div>
					<div className={styles.modalBody}>
						<p>{message}</p>
					</div>
					<div className={styles.modalFooter}>
						<button
							type="button"
							className={`${styles.btn} ${styles.btnSecondary} ${cancelButtonClass}`}
							onClick={onCancel}
						>
							{cancelText}
						</button>
						<button
							type="button"
							className={`${styles.btn} ${styles.btnPrimary} ${confirmButtonClass}`}
							onClick={onConfirm}
						>
							{confirmText}
						</button>
					</div>
				</div>
			</div>
		</div>
	);
};

export default ConfirmModal;
