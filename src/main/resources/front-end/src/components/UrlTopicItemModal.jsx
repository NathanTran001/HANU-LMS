import React, { useState, useEffect, useRef } from "react";
import api from "../services/apiService";
import getErrorMessages from "../utils/error";
import styles from "./styles/CreateTopicItemModal.module.css";

const UrlTopicItemModal = ({
	show,
	onClose,
	topicId, // optional, if present: create mode
	item, // optional, if present: edit mode
}) => {
	const [title, setTitle] = useState(item?.title || "");
	const [url, setUrl] = useState(item?.url || "");
	const [loading, setLoading] = useState(false);
	const [errors, setErrors] = useState([]);
	const modalRef = useRef();

	// Reset form when opening/closing or switching item
	useEffect(() => {
		setTitle(item?.title || "");
		setUrl(item?.url || "");
		setErrors([]);
		setLoading(false);
	}, [show, item]);

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

	const handleSubmit = async (e) => {
		e.preventDefault();
		setLoading(true);
		setErrors([]);
		try {
			if (item) {
				// Edit mode
				await api.updateTopicItem(item.id, title, url);
			} else {
				// Create mode
				await api.createUrlTopicItem(title, url, topicId);
			}
			onClose();
		} catch (err) {
			setErrors(getErrorMessages(err));
		} finally {
			setLoading(false);
		}
	};

	return (
		<div className={styles.modalOverlay}>
			<div
				className={styles.modal}
				ref={modalRef}
			>
				<div className={styles.modalHeader}>
					<h4>{item ? "Edit" : "Create"} URL Topic Item</h4>
				</div>
				<form
					className={styles.form}
					onSubmit={handleSubmit}
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
								? item
									? "Saving..."
									: "Creating..."
								: item
								? "Save"
								: "Create"}
						</button>
						<button
							type="button"
							className={styles.cancelBtn}
							onClick={onClose}
							disabled={loading}
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
