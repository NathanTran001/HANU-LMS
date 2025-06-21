import React, { useState, useRef, useEffect } from "react";
import styles from "./styles/CreateTopicItemModal.module.css";
import UrlTopicItemModal from "./UrlTopicItemModal";
import FileTopicItemModal from "./FileTopicItemModal";
import FolderTopicItemModal from "./FolderTopicItemModal";

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

const CreateTopicItemModal = ({ topicId, show, onClose }) => {
	const [step, setStep] = useState("select"); // select | url | file | folder
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
		onClose();
	};

	const handleBack = () => {
		setStep("select");
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
					<UrlTopicItemModal
						show={true}
						onClose={handleBack}
						topicId={topicId}
					/>
				)}
				{step === "file" && (
					<FileTopicItemModal
						show={true}
						onClose={handleBack}
						topicId={topicId}
					/>
				)}
				{step === "folder" && (
					<FolderTopicItemModal
						show={true}
						onClose={handleBack}
						topicId={topicId}
					/>
				)}
			</div>
		</div>
	);
};

export default CreateTopicItemModal;
