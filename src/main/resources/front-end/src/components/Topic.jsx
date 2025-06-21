import { useState, useRef, useEffect } from "react";
import TopicItem from "./TopicItem";
import styles from "../pages/styles/CourseDetailPage.module.css";
import CreateTopicItemModal from "./CreateTopicItemModal";
import ConfirmModal from "./ConfirmModal";
import api from "../services/apiService";
import getErrorMessages from "../utils/error";

const Topic = ({ topic, canEdit, onEdit, onDelete, fetchCourse }) => {
	const [open, setOpen] = useState(false);
	const contentRef = useRef(null);
	const [maxHeight, setMaxHeight] = useState("0px");
	const [showModal, setShowModal] = useState(false);
	const [deleteItemId, setDeleteItemId] = useState(null);
	const [deleting, setDeleting] = useState(false);
	const [errors, setErrors] = useState([]);

	useEffect(() => {
		if (open && contentRef.current) {
			setMaxHeight(contentRef.current.scrollHeight + "px");
		} else {
			setMaxHeight("0px");
		}
	}, [open, topic]);

	const handleDelete = async () => {
		setDeleting(true);
		setErrors([]);
		try {
			await api.deleteTopicItem(deleteItemId);
			setDeleteItemId(null);
			if (fetchCourse) fetchCourse();
		} catch (error) {
			setErrors(getErrorMessages(error));
		} finally {
			setDeleting(false);
		}
	};

	return (
		<div className={styles.accordionSection}>
			<div
				className={styles.accordionHeader}
				onClick={() => setOpen((prev) => !prev)}
			>
				<span className={styles.chevronWrapper}>
					<i
						className={`bi bi-chevron-right ${styles.chevronIcon} ${
							open ? styles.chevronOpen : ""
						}`}
						aria-hidden="true"
					/>
					{topic.title}
				</span>
				{canEdit && (
					<span style={{ float: "right" }}>
						<button
							className={styles.actionBtn}
							title="Create Topic Item"
							onClick={() => setShowModal(true)}
						>
							<i className="bi bi-plus-lg"></i>
						</button>
						<button
							className={styles.actionBtn}
							title="Edit"
							onClick={(e) => {
								e.stopPropagation();
								onEdit();
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
			</div>
			<div
				ref={contentRef}
				className={`${styles.accordionContent} ${open ? styles.open : ""}`}
				style={{
					maxHeight,
					opacity: open ? 1 : 0,
				}}
			>
				{errors &&
					errors.map(
						(err, i) =>
							err && (
								<div
									key={i}
									className={styles.error}
								>
									{err}
								</div>
							)
					)}
				{topic.topicItems?.length > 0 ? (
					topic.topicItems?.map((item) => (
						<TopicItem
							key={item.id}
							item={item}
							canEdit={canEdit}
							onDelete={() => setDeleteItemId(item.id)}
							onUpdated={fetchCourse}
						/>
					))
				) : (
					<div
						style={{
							paddingBlock: "1rem",
						}}
					>
						No course materials uploaded yet
					</div>
				)}
			</div>
			<CreateTopicItemModal
				topicId={topic.id}
				show={showModal}
				onClose={() => {
					setShowModal(false);
					fetchCourse();
				}}
			/>
			<ConfirmModal
				isOpen={!!deleteItemId}
				title="Delete Topic Item"
				message="Are you sure you want to delete this topic item? This action cannot be undone."
				onConfirm={handleDelete}
				onCancel={() => setDeleteItemId(null)}
				confirmText={deleting ? "Deleting..." : "Delete"}
				cancelText="Cancel"
				confirmButtonClass="btn-danger"
			/>
		</div>
	);
};

export default Topic;
