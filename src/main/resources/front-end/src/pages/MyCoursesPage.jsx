import React, { useState, useEffect } from "react";
import { getUserRole } from "../utils/auth";
import styles from "./styles/MyCoursesPage.module.css";
import api from "../services/apiService";

const MyCoursesPage = () => {
	const [courses, setCourses] = useState([]);
	const [message, setMessage] = useState("");
	const [userRole, setUserRole] = useState("");
	const [activeModal, setActiveModal] = useState(null);

	useEffect(() => {
		// Get user role on component mount
		const role = getUserRole();
		setUserRole(role);

		// TODO: Fetch courses data from your API
		// This is placeholder data - replace with your actual API call
		fetchCourses();
	}, []);

	const fetchCourses = async () => {
		try {
			// Replace this with your actual API call
			// const response = await fetch('/api/courses');
			// const coursesData = await response.json();

			// Placeholder data for demonstration
			const coursesData = api.getCourses();

			setCourses(Array.isArray(coursesData) ? coursesData : []);
		} catch (error) {
			console.error("Error fetching courses:", error);
			setMessage("Error loading courses");
		}
	};

	const onEditClick = (courseCode) => {
		window.location.href = `/editCourse/${courseCode}`;
	};

	const onCreateClick = () => {
		window.location.href = "/createCourse";
	};

	const onDeleteClick = (courseCode) => {
		window.location.href = `/deleteCourse/${courseCode}`;
	};

	const openModal = (courseCode) => {
		setActiveModal(courseCode);
	};

	const closeModal = () => {
		setActiveModal(null);
	};

	const isLecturer = userRole === "LECTURER";

	return (
		<div className={styles.containerLg}>
			<div className={styles.pathContainer}>
				<a
					href=""
					className={styles.path}
				>
					Home
				</a>{" "}
				/
				<a
					href=""
					className={styles.path}
				>
					My Courses
				</a>
			</div>

			<div className={`${styles.titleContainer} ${styles.mb3}`}>
				<h1 className={styles.title}>MY COURSES</h1>
				{isLecturer && (
					<button
						className={styles.baseButton}
						onClick={onCreateClick}
					>
						Create
					</button>
				)}
			</div>

			{message && (
				<div
					className={styles.textSuccess}
					id="messages"
				>
					{message}
				</div>
			)}

			{courses.map((course) => (
				<div
					key={course.code}
					className={styles.courseItemContainer}
				>
					<div className={styles.courseTitleContainer}>
						<a
							className={`${styles.courseTitle} ${styles.hyperlink}`}
							href={`/myCourses/course-details/${course.code}`}
						>
							{course.code} - {course.name}
						</a>

						{isLecturer && (
							<div className={styles.buttonsContainer}>
								<button
									className={styles.baseButton}
									onClick={() => onEditClick(course.code)}
								>
									Edit
								</button>
								<button
									className={styles.baseButton}
									onClick={() => openModal(course.code)}
								>
									Delete
								</button>
							</div>
						)}
					</div>

					<p className={styles.courseDescription}>{course.description}</p>

					<div className={styles.lecturers}>
						{course.lecturers.map((lecturer, index) => (
							<span key={index}>
								<span>{lecturer.name}</span>
								{index < course.lecturers.length - 1 && (
									<span className={styles.abc}> | </span>
								)}
							</span>
						))}
					</div>

					{/* Modal */}
					{activeModal === course.code && (
						<div
							className={styles.modalOverlay}
							onClick={closeModal}
						>
							<div
								className={styles.modal}
								onClick={(e) => e.stopPropagation()}
							>
								<div className={styles.modalContent}>
									<div className={styles.modalHeader}>
										<h5 className={styles.modalTitle}>Delete Course</h5>
										<button
											type="button"
											className={styles.closeButton}
											onClick={closeModal}
										>
											<span>&times;</span>
										</button>
									</div>
									<div className={styles.modalBody}>
										Do you want to delete this course?
									</div>
									<div className={styles.modalFooter}>
										<button
											type="button"
											className={`${styles.btn} ${styles.btnSecondary}`}
											onClick={closeModal}
										>
											Close
										</button>
										<button
											className={`${styles.btn} ${styles.btnDanger}`}
											onClick={() => onDeleteClick(course.code)}
										>
											Delete
										</button>
									</div>
								</div>
							</div>
						</div>
					)}
				</div>
			))}
		</div>
	);
};

export default MyCoursesPage;
