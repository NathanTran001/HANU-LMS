import { useState, useEffect } from "react";
import styles from "./styles/CourseDetailPage.module.css";
import api from "../services/apiService";
import { Link } from "react-router-dom";

const CourseDetailPage = ({ courseId }) => {
	const [course, setCourse] = useState(null);
	const [openTopicId, setOpenTopicId] = useState(null);

	useEffect(() => {
		const fetchCourse = async () => {
			const data = await api.getCourseDetail(courseId);
			setCourse(data);
		};
		fetchCourse();
	}, [courseId]);

	const toggleAccordion = (topicId) => {
		setOpenTopicId(openTopicId === topicId ? null : topicId);
	};

	const renderTopicItem = (item) => {
		switch (item.type) {
			case "url":
				return (
					<div
						key={item.id}
						className={styles.topicItem}
					>
						<span className={styles.typeLabel}>URL</span>
						<Link
							to={item.link}
							target="_blank"
							rel="noopener noreferrer"
						>
							{item.name}
						</Link>
					</div>
				);
			case "file":
			case "folder":
				return (
					<div
						key={item.id}
						className={styles.topicItem}
					>
						<span className={styles.typeLabel}>
							{item.type === "file" ? "FILE" : "FOLDER"}
						</span>
						<a
							href={item.downloadLink}
							download
						>
							{item.name}
						</a>
					</div>
				);
			default:
				return (
					<div
						key={item.id}
						className={styles.topicItem}
					>
						<span className={styles.typeLabel}>UNKNOWN</span>
						<span>{item.name}</span>
					</div>
				);
		}
	};

	if (!course) return <div>Loading...</div>;

	return (
		<div className={styles.courseDetailPage}>
			{course.topics.map((topic) => (
				<div
					key={topic.id}
					className={styles.accordionSection}
				>
					<button
						className={styles.accordionHeader}
						onClick={() => toggleAccordion(topic.id)}
					>
						WEEK {topic.weekNumber} : {topic.title}
					</button>
					{openTopicId === topic.id && (
						<div className={styles.accordionContent}>
							{topic.items.map((item) => renderTopicItem(item))}
						</div>
					)}
				</div>
			))}
		</div>
	);
};

export default CourseDetailPage;
