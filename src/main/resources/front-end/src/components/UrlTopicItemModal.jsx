import React, { useState, useEffect, useRef } from "react";
import styles from "../pages/styles/CreateTopicItemModal.module.css";

const UrlTopicItemModal = ({
	show,
	onClose,
	onSubmit,
	initialValues = { title: "", url: "" },
	loading = false,
	isEdit = false,
}) => {
	const [title, setTitle] = useState(initialValues.title);
	const [url, setUrl] = useState(initialValues.url);
	const modalRef = useRef();
	const [errors, setErrors] = useState([]);

	useEffect(() => {
		if (show) {
			setTitle(initialValues.title);
			setUrl(initialValues.url);
		}
	}, [show, initialValues]);

	// Close modal on outside click
	useEffect(() => {
		if (!show) return;
		const handleClick = (e) => {
			if (modalRef.current && !modalRef.current.contains(e.target)) {
				onClose();
			}
		};
		document.addEventListener("mousedown", handleClick);
		return () => document.removeEventListener("mousedown", handleClick);
	}, [show, onClose]);

	if (!show) return null;

	return (
		<div className={styles.modalOverlay}>
			<div
				className={styles.modal}
				ref={modalRef}
			>
				<div className={styles.modalHeader}>
					<h4>{isEdit ? "Edit" : "Create"} URL Topic Item</h4>
				</div>
				<form
					className={styles.form}
					onSubmit={(e) => {
						e.preventDefault();
						onSubmit({ title, url });
					}}
				>
					<input
						type="text"
						placeholder="Title"
						value={title}
						onChange={(e) => setTitle(e.target.value)}
						required
						className={styles.input}
					/>
					<input
						type="url"
						placeholder="URL"
						value={url}
						onChange={(e) => setUrl(e.target.value)}
						required
						className={styles.input}
					/>
					{errors.map((err, i) => (
						<div
							key={i}
							className={styles.error}
						>
							{err}
						</div>
					))}
					<div style={{ display: "flex", gap: "1rem", marginTop: "1rem" }}>
						<button
							type="submit"
							className={styles.submitBtn}
							disabled={loading}
						>
							{loading
								? isEdit
									? "Saving..."
									: "Creating..."
								: isEdit
								? "Save"
								: "Create"}
						</button>
						<button
							type="button"
							className={styles.cancelBtn}
							onClick={onClose}
						>
							Cancel
						</button>
					</div>
				</form>
			</div>
		</div>
	);
};

export default UrlTopicItemModal;
