import { useState } from "react";
import styles from "./styles/TopicItem.module.css";
import api from "../services/apiService";
import FileTopicItemModal from "./FileTopicItemModal";
import UrlTopicItemModal from "./UrlTopicItemModal";
import FolderTopicItemModal from "./FolderTopicItemModal";

const TopicItem = ({ item, canEdit = false, onDelete, onUpdated }) => {
	const [isDownloading, setIsDownloading] = useState(false);
	const [showEditModal, setShowEditModal] = useState(false);

	const handleFileDownload = async (e) => {
		e.preventDefault();
		e.stopPropagation();
		setIsDownloading(true);

		try {
			const data = await api.getTopicItemDownloadUrl(item.id);

			if (data.downloadUrl) {
				// Create a temporary link and trigger download
				const link = document.createElement("a");
				link.href = data.downloadUrl;
				link.download = item.originalFilename || item.title;
				link.target = "_blank"; // Extra safety
				link.rel = "noopener noreferrer";

				// Make it invisible
				link.style.display = "none";

				document.body.appendChild(link);
				link.click();

				// Clean up after a short delay
				setTimeout(() => {
					document.body.removeChild(link);
				}, 100);
			} else {
				console.error("Failed to get download URL");
				alert("Failed to download file: Unknown error");
			}
		} catch (error) {
			console.error("Download error:", error);
			alert("Failed to download file");
		} finally {
			setIsDownloading(false);
		}
	};

	const handleEditClick = () => {
		setShowEditModal(true);
	};

	const handleEditClose = () => {
		setShowEditModal(false);
		if (onUpdated) onUpdated(); // Refresh parent if needed
	};

	let EditModal = null;
	if (showEditModal) {
		if (item.type === "URL") {
			EditModal = (
				<UrlTopicItemModal
					show={true}
					onClose={handleEditClose}
					item={item}
				/>
			);
		} else if (item.type === "FILE") {
			EditModal = (
				<FileTopicItemModal
					show={true}
					onClose={handleEditClose}
					item={item}
				/>
			);
		} else if (item.type === "FOLDER") {
			EditModal = (
				<FolderTopicItemModal
					show={true}
					onClose={handleEditClose}
					item={item}
				/>
			);
		}
	}

	switch (item.type) {
		case "URL":
			return (
				<div className={styles.topicItem}>
					<span className={styles.type}>
						<i className="bi bi-link"></i>
					</span>
					<a
						href={item.url}
						target="_blank"
						rel="noopener noreferrer"
						style={{
							cursor: "pointer",
							background: "none",
							border: "none",
							padding: 0,
							font: "inherit",
							color: "inherit",
							textDecoration: "underline",
							flex: 1,
							textAlign: "left",
						}}
					>
						{item.title}
					</a>
					{canEdit && (
						<span className={styles.actionBtnContainer}>
							<button
								className={styles.actionBtn}
								title="Edit"
								onClick={(e) => {
									e.stopPropagation();
									handleEditClick();
								}}
							>
								<i className="bi bi-pencil-square"></i>
							</button>
							<button
								className={`${styles.actionBtn} ${styles.deleteBtn}`}
								title="Delete"
								onClick={(e) => {
									e.stopPropagation();
									onDelete();
								}}
							>
								<i className="bi bi-trash3-fill"></i>
							</button>
						</span>
					)}
					{EditModal}
				</div>
			);
		case "FILE":
		case "FOLDER":
			return (
				<div className={styles.topicItem}>
					<span className={styles.type}>
						{item.type === "FILE" ? (
							<i className="bi bi-file-earmark-fill"></i>
						) : (
							<i className="bi bi-folder-fill"></i>
						)}
					</span>
					<button
						onClick={handleFileDownload}
						disabled={isDownloading}
						style={{
							cursor: isDownloading ? "wait" : "pointer",
							background: "none",
							border: "none",
							padding: 0,
							font: "inherit",
							color: isDownloading ? "#999" : "inherit",
							textDecoration: "underline",
							flex: 1,
							textAlign: "left",
						}}
					>
						{isDownloading ? "Downloading..." : item.title}
					</button>
					{canEdit && (
						<span className={styles.actionBtnContainer}>
							<button
								className={styles.actionBtn}
								title="Edit"
								onClick={(e) => {
									e.stopPropagation();
									handleEditClick();
								}}
							>
								<i className="bi bi-pencil-square"></i>
							</button>
							<button
								className={`${styles.actionBtn} ${styles.deleteBtn}`}
								title="Delete"
								onClick={(e) => {
									e.stopPropagation();
									onDelete();
								}}
							>
								<i className="bi bi-trash3-fill"></i>
							</button>
						</span>
					)}
					{EditModal}
				</div>
			);
		default:
			return (
				<div className={styles.topicItem}>
					<span className={styles.typeLabel}>UNKNOWN</span>
					<span>{item.title}</span>
				</div>
			);
	}
};

export default TopicItem;
