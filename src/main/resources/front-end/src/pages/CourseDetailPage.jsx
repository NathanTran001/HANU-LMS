import { useState, useEffect, useRef } from "react";
import styles from "./styles/CourseDetailPage.module.css";
import api from "../services/apiService";
import { useParams } from "react-router-dom";
import Topic from "../components/Topic";
import { useAuth } from "../contexts/AuthContext";
import { LECTURER } from "../constants/roles";
import getErrorMessages from "../utils/error";
import CourseAnnouncement from "../components/CourseAnnouncement";

const CourseDetailPage = () => {
	const [course, setCourse] = useState(null);
	const [creating, setCreating] = useState(false);
	const [newTitle, setNewTitle] = useState("");
	const [editingId, setEditingId] = useState(null);
	const [editingTitle, setEditingTitle] = useState("");
	const { code: courseCode } = useParams();
	const { user } = useAuth();
	const createRef = useRef(null);
	const [errors, setErrors] = useState([]);

	useEffect(() => {
		fetchCourse();
	}, [courseCode]);

	const fetchCourse = async () => {
		const data = await api.getCourse(courseCode);
		setCourse(data);
	};

	// Click outside to cancel create
	useEffect(() => {
		if (!creating) return;
		const handleClick = (e) => {
			if (createRef.current && !createRef.current.contains(e.target)) {
				setCreating(false);
				setNewTitle("");
			}
		};
		document.addEventListener("mousedown", handleClick);
		return () => document.removeEventListener("mousedown", handleClick);
	}, [creating]);

	const handleCreateTopic = () => {
		setCreating(true);
		setNewTitle("");
	};

	const handleCreateSave = async () => {
		if (!newTitle.trim()) return;
		try {
			const newTopic = await api.createTopic(courseCode, { title: newTitle });
			fetchCourse();
		} catch (error) {
			setErrors(getErrorMessages(error));
		} finally {
			setCreating(false);
			setNewTitle("");
		}
	};

	const handleEdit = (id, title) => {
		setEditingId(id);
		setEditingTitle(title);
	};

	const handleEditSave = async (topic) => {
		if (!editingTitle.trim()) return;
		try {
			const updated = await api.updateTopic(topic.id, { title: editingTitle });
			fetchCourse();
		} catch (error) {
			setErrors(getErrorMessages(error));
		} finally {
			setEditingId(null);
			setEditingTitle("");
		}
	};

	const handleDelete = async (id) => {
		if (!window.confirm("Delete this topic?")) return;
		try {
			await api.deleteTopic(id);
			fetchCourse();
		} catch (error) {
			setErrors(getErrorMessages(error));
		}
	};

	if (!course) return <div>Loading...</div>;

	return (
		<div className={styles.courseDetailPage}>
			<h3 className={styles.pageTitle}>
				{course.code} - {course.name}
			</h3>
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
			<CourseAnnouncement
				canEdit={user?.role === LECTURER}
				announcement={course?.announcement}
				fetchCourse={fetchCourse}
				courseCode={course?.code}
			/>
			<h4 className={styles.pageTitle}>Course Materials</h4>
			{user?.role === LECTURER && (
				<button
					className={styles.createTopicBtn}
					onClick={handleCreateTopic}
					disabled={creating}
				>
					+ Create Topic
				</button>
			)}

			{/* Dummy accordion for creating topic */}
			{creating && (
				<div
					className={styles.accordionSection}
					ref={createRef}
				>
					<div className={styles.accordionHeader}>
						<input
							autoFocus
							className={styles.topicInput}
							type="text"
							placeholder="Enter topic title"
							value={newTitle}
							onChange={(e) => setNewTitle(e.target.value)}
							onKeyDown={(e) => {
								if (e.key === "Enter") handleCreateSave();
								if (e.key === "Escape") {
									setCreating(false);
									setNewTitle("");
								}
							}}
						/>
					</div>
				</div>
			)}

			{course.topics?.map((topic) =>
				editingId === topic.id ? (
					<div
						className={styles.accordionSection}
						key={topic.id}
					>
						<div className={styles.accordionHeader}>
							<input
								autoFocus
								className={styles.topicInput}
								type="text"
								value={editingTitle}
								onChange={(e) => setEditingTitle(e.target.value)}
								onKeyDown={(e) => {
									if (e.key === "Enter") handleEditSave(topic);
									if (e.key === "Escape") setEditingId(null);
								}}
							/>
						</div>
					</div>
				) : (
					<Topic
						key={topic.id}
						topic={topic}
						canEdit={user?.role === LECTURER}
						onEdit={() => handleEdit(topic.id, topic.title)}
						onDelete={() => handleDelete(topic.id)}
						fetchCourse={fetchCourse}
					/>
				)
			)}
		</div>
	);
};

export default CourseDetailPage;
