import React, { useEffect, useState } from "react";
import api from "../services/apiService";
import styles from "./styles/CourseAnnouncement.module.css";
import getErrorMessages from "../utils/error";
import TextField from "./TextField";

const CourseAnnouncement = ({
	announcement,
	fetchCourse,
	canEdit,
	courseCode,
}) => {
	const [editing, setEditing] = useState(false);
	const [form, setForm] = useState({
		title: announcement?.title || "",
		description: announcement?.description || "",
	});
	const [loading, setLoading] = useState(false);
	const [error, setError] = useState("");

	const handleChange = (e) => {
		setForm({ ...form, [e.target.name]: e.target.value });
	};

	const handleCreate = async () => {
		setLoading(true);
		setError("");
		try {
			await api.createCourseAnnouncement(courseCode, form);
			setEditing(false);
			fetchCourse();
		} catch (err) {
			setError(getErrorMessages(err));
		}
		setLoading(false);
	};

	const handleUpdate = async () => {
		setLoading(true);
		setError("");
		try {
			await api.updateCourseAnnouncement(announcement.id, form);
			setEditing(false);
			fetchCourse();
		} catch (err) {
			setError(getErrorMessages(err));
		}
		setLoading(false);
	};

	const handleDelete = async () => {
		if (!window.confirm("Delete this announcement?")) return;
		setLoading(true);
		setError("");
		try {
			await api.deleteCourseAnnouncement(announcement.id);
			fetchCourse();
		} catch (err) {
			setError(getErrorMessages(err));
		}
		setLoading(false);
	};

	// Reset form when announcement changes
	useEffect(() => {
		setForm({
			title: announcement?.title || "",
			description: announcement?.description || "",
		});
		setEditing(false);
	}, [announcement]);

	return (
		<div className={styles.announcementContainer}>
			<h2 className={styles.sectionTitle}>Course Announcement</h2>
			{announcement && !editing ? (
				<>
					<h3 className={styles.title}>{announcement.title}</h3>
					<p className={styles.description}>{announcement.description}</p>
					{canEdit && (
						<div className={styles.buttonGroup}>
							<button
								className={styles.baseButton}
								onClick={() => setEditing(true)}
								disabled={loading}
							>
								Edit
							</button>
							<button
								className={styles.deleteBtn}
								onClick={handleDelete}
								disabled={loading}
							>
								Delete
							</button>
						</div>
					)}
				</>
			) : canEdit && editing ? (
				<div className={styles.form}>
					<TextField
						label={"Title"}
						placeholder="Upcoming Exams..."
						value={form.title}
						onChange={(value) => setForm({ ...form, title: value })}
					/>
					<TextField
						label={"Description"}
						placeholder="We will have an mock exam on 13/4..."
						value={form.description}
						onChange={(value) => setForm({ ...form, description: value })}
						multiline
					/>
					<div className={styles.buttonGroup}>
						{announcement ? (
							<>
								<button
									className={styles.baseButton}
									onClick={handleUpdate}
									disabled={loading}
								>
									Save
								</button>
								<button
									className={styles.baseButton}
									onClick={() => setEditing(false)}
									disabled={loading}
								>
									Cancel
								</button>
							</>
						) : (
							<>
								<button
									className={styles.baseButton}
									onClick={handleCreate}
									disabled={loading}
								>
									Create
								</button>
								<button
									className={styles.baseButton}
									onClick={() => setEditing(false)}
									disabled={loading}
								>
									Cancel
								</button>
							</>
						)}
					</div>
					{error && <div className={styles.error}>{error}</div>}
				</div>
			) : (
				<p className={styles.noAnnouncement}>No announcement.</p>
			)}
			{canEdit && !announcement && !editing && (
				<button
					onClick={() => setEditing(true)}
					className={styles.baseButton}
					disabled={!!announcement}
				>
					Create Announcement
				</button>
			)}
		</div>
	);
};

export default CourseAnnouncement;
