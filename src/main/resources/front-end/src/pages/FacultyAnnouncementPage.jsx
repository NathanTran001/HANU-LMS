import React, { useEffect, useState } from "react";
import styles from "./styles/FacultyAnnouncementPage.module.css";
import TextField from "../components/TextField";
import { useAuth } from "../contexts/AuthContext";
import api from "../services/apiService";
import getErrorMessages from "../utils/error";

const FacultyAnnouncementPage = () => {
	const { user } = useAuth();
	const isLecturer = user?.role === "LECTURER";
	const [announcements, setAnnouncements] = useState([]);
	const [loading, setLoading] = useState(true);
	const [errors, setErrors] = useState([]);
	const [form, setForm] = useState({ title: "", description: "" });
	const [formErrors, setFormErrors] = useState([]);
	const [isSubmitting, setIsSubmitting] = useState(false);
	const [editingId, setEditingId] = useState(null);

	const fetchAnnouncements = async () => {
		setLoading(true);
		try {
			const data = await api.getFacultyAnnouncements();
			setAnnouncements(data);
		} catch (error) {
			setErrors(getErrorMessages(error));
		} finally {
			setLoading(false);
		}
	};

	useEffect(() => {
		fetchAnnouncements();
	}, []);

	const handleInputChange = (value, field) => {
		setForm({ ...form, [field]: value });
	};

	const handleSubmit = async (e) => {
		e.preventDefault();
		setIsSubmitting(true);
		setFormErrors([]);
		try {
			if (editingId) {
				await api.updateFacultyAnnouncement(editingId, form);
			} else {
				await api.createFacultyAnnouncement(form);
			}
			setForm({ title: "", description: "" });
			setEditingId(null);
			await fetchAnnouncements();
		} catch (error) {
			setFormErrors(getErrorMessages(error));
		} finally {
			setIsSubmitting(false);
		}
	};

	const handleEdit = (announcement) => {
		setEditingId(announcement.id);
		setForm({
			title: announcement.title,
			description: announcement.description,
		});
		setFormErrors([]);
	};

	const handleDelete = async (id) => {
		if (!window.confirm("Are you sure you want to delete this announcement?"))
			return;
		try {
			await api.deleteFacultyAnnouncement(id);
			await fetchAnnouncements();
		} catch (error) {
			setErrors(getErrorMessages(error));
		}
	};

	const handleCancelEdit = () => {
		setEditingId(null);
		setForm({ title: "", description: "" });
		setFormErrors([]);
	};

	return (
		<div className={styles.container}>
			<h2 className="pageTitle">Faculty Announcements</h2>
			{errors.map((err, i) => (
				<div
					key={i}
					className="error"
				>
					{err}
				</div>
			))}
			{isLecturer && (
				<div className={styles.formSection}>
					<h3 className={styles.formTitle}>
						{editingId ? "Edit Announcement" : "Create Announcement"}
					</h3>
					<form
						onSubmit={handleSubmit}
						className={styles.form}
					>
						<TextField
							label="Title"
							required
							value={form.title}
							onChange={(v) => handleInputChange(v, "title")}
							icon="megaphone"
							placeholder="Enter announcement title"
						/>
						<TextField
							label="Description"
							required
							value={form.description}
							onChange={(v) => handleInputChange(v, "description")}
							placeholder="Enter announcement description"
							multiline
						/>
						{formErrors.map((err, i) => (
							<div
								key={i}
								className={styles.error}
							>
								{err}
							</div>
						))}
						<div className={styles.formActions}>
							<button
								type="submit"
								className={styles.baseButton}
								disabled={isSubmitting}
							>
								{isSubmitting
									? editingId
										? "Updating..."
										: "Creating..."
									: editingId
									? "Update"
									: "Create"}
							</button>
							{editingId && (
								<button
									type="button"
									className={styles.baseButton}
									style={{
										marginLeft: 8,
										background: "var(--primary100)",
										color: "var(--primary800)",
									}}
									onClick={handleCancelEdit}
								>
									Cancel
								</button>
							)}
						</div>
					</form>
				</div>
			)}
			<div className={styles.listSection}>
				{loading ? (
					<div>Loading...</div>
				) : announcements.length === 0 ? (
					<div>No announcements yet.</div>
				) : (
					<ul className={styles.announcementList}>
						{announcements.map((a) => (
							<li
								key={a.id}
								className={styles.announcementItem}
							>
								<div className={styles.announcementHeader}>
									<span className={styles.announcementTitle}>{a.title}</span>
									{isLecturer && (
										<div className={styles.announcementActions}>
											<button
												className={styles.actionBtn}
												title="Edit"
												onClick={() => handleEdit(a)}
											>
												<i className="bi bi-pencil-square"></i>
											</button>
											<button
												className={styles.deleteBtn + " " + styles.actionBtn}
												title="Delete"
												onClick={() => handleDelete(a.id)}
											>
												<i className="bi bi-trash"></i>
											</button>
										</div>
									)}
								</div>
								<div className={styles.announcementDescription}>
									{a.description}
								</div>
							</li>
						))}
					</ul>
				)}
			</div>
		</div>
	);
};

export default FacultyAnnouncementPage;
