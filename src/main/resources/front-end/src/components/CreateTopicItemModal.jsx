import React, { useState, useRef, useEffect } from "react";
import styles from "./styles/CreateTopicItemModal.module.css";
import api from "../services/apiService";
import getErrorMessages from "../utils/error";

const ICONS = {
	URL: "bi-link-45deg",
	FILE: "bi-file-earmark-arrow-up",
	FOLDER: "bi-folder-plus",
};

const LABELS = {
	URL: "URL",
	FILE: "File",
	FOLDER: "Folder",
};

const CreateTopicItemModal = ({ topicId, show, onClose, onCreated }) => {
	const [step, setStep] = useState("select"); // select | url | file | folder
	const [errors, setErrors] = useState([]);
	const [loading, setLoading] = useState(false);

	// Form states
	const [urlTitle, setUrlTitle] = useState("");
	const [url, setUrl] = useState("");
	const [fileTitle, setFileTitle] = useState("");
	const [file, setFile] = useState(null);
	const [folderTitle, setFolderTitle] = useState("");
	const [folder, setFolder] = useState(null);

	const modalRef = useRef();

	// Close modal on outside click
	useEffect(() => {
		if (!show) return;
		const handleClick = (e) => {
			if (modalRef.current && !modalRef.current.contains(e.target)) {
				handleClose();
			}
		};
		document.addEventListener("mousedown", handleClick);
		return () => document.removeEventListener("mousedown", handleClick);
	}, [show]);

	const handleClose = () => {
		setStep("select");
		setErrors([]);
		setUrlTitle("");
		setUrl("");
		setFileTitle("");
		setFile(null);
		setFolderTitle("");
		setFolder(null);
		onClose();
	};

	const handleBack = () => {
		setStep("select");
		setErrors([]);
	};

	const handleCreateUrl = async (e) => {
		e.preventDefault();
		setLoading(true);
		setErrors([]);
		try {
			let title = urlTitle;
			await api.createUrlTopicItem(title, url, topicId);
			if (onCreated) onCreated();
			handleClose();
		} catch (err) {
			setErrors(getErrorMessages(err));
		} finally {
			setLoading(false);
		}
	};

	const handleCreateFile = async (e) => {
		e.preventDefault();
		if (!file) {
			setErrors(["Please select a file."]);
			return;
		}
		setLoading(true);
		setErrors([]);
		try {
			let title = fileTitle;
			await api.createFileTopicItem(title, file, topicId);
			if (onCreated) onCreated();
			handleClose();
		} catch (err) {
			setErrors(getErrorMessages(err));
		} finally {
			setLoading(false);
		}
	};

	const handleCreateFolder = async (e) => {
		e.preventDefault();
		if (!folder) {
			setErrors(["Please select a ZIP file."]);
			return;
		}
		setLoading(true);
		setErrors([]);
		try {
			let title = folderTitle;
			await api.createFolderTopicItem(title, folder, topicId);
			if (onCreated) onCreated();
			handleClose();
		} catch (err) {
			setErrors(getErrorMessages(err));
		} finally {
			setLoading(false);
		}
	};

	if (!show) return null;

	return (
		<div className={styles.modalOverlay}>
			<div
				className={styles.modal}
				ref={modalRef}
			>
				{step === "select" && (
					<>
						<div className={styles.modalHeader}>
							<h4>Create Topic Item</h4>
						</div>
						<div className={styles.typeRow}>
							{["URL", "FILE", "FOLDER"].map((type) => (
								<div
									key={type}
									className={styles.typeOption}
									onClick={() => setStep(type.toLowerCase())}
								>
									<i className={`bi ${ICONS[type]} ${styles.typeIcon}`} />
									<div className={styles.typeLabel}>{LABELS[type]}</div>
								</div>
							))}
						</div>
						<button
							className={styles.cancelBtn}
							onClick={handleClose}
						>
							Cancel
						</button>
					</>
				)}

				{step === "url" && (
					<form
						className={styles.form}
						onSubmit={handleCreateUrl}
					>
						<div className={styles.modalHeader}>
							<button
								type="button"
								className={styles.backBtn}
								onClick={handleBack}
							>
								<i className="bi bi-arrow-left" /> Back
							</button>
							<h4>Create URL Topic Item</h4>
						</div>
						<input
							type="text"
							placeholder="Title"
							value={urlTitle}
							onChange={(e) => setUrlTitle(e.target.value)}
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
						<button
							type="submit"
							className={styles.submitBtn}
							disabled={loading}
						>
							{loading ? "Creating..." : "Create"}
						</button>
					</form>
				)}

				{step === "file" && (
					<form
						className={styles.form}
						onSubmit={handleCreateFile}
					>
						<div className={styles.modalHeader}>
							<button
								type="button"
								className={styles.backBtn}
								onClick={handleBack}
							>
								<i className="bi bi-arrow-left" /> Back
							</button>
							<h4>Create File Topic Item</h4>
						</div>
						<input
							type="text"
							placeholder="Title"
							value={fileTitle}
							onChange={(e) => setFileTitle(e.target.value)}
							required
							className={styles.input}
						/>
						<input
							type="file"
							onChange={(e) => setFile(e.target.files[0])}
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
						<button
							type="submit"
							className={styles.submitBtn}
							disabled={loading}
						>
							{loading ? "Creating..." : "Create"}
						</button>
					</form>
				)}

				{step === "folder" && (
					<form
						className={styles.form}
						onSubmit={handleCreateFolder}
					>
						<div className={styles.modalHeader}>
							<button
								type="button"
								className={styles.backBtn}
								onClick={handleBack}
							>
								<i className="bi bi-arrow-left" /> Back
							</button>
							<h4>Create Folder Topic Item</h4>
						</div>
						<input
							type="text"
							placeholder="Title"
							value={folderTitle}
							onChange={(e) => setFolderTitle(e.target.value)}
							required
							className={styles.input}
						/>
						<input
							type="file"
							accept=".zip"
							onChange={(e) => setFolder(e.target.files[0])}
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
						<button
							type="submit"
							className={styles.submitBtn}
							disabled={loading}
						>
							{loading ? "Creating..." : "Create"}
						</button>
					</form>
				)}
			</div>
		</div>
	);
};

export default CreateTopicItemModal;
