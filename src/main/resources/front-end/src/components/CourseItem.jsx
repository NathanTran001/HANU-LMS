import { useState } from "react";
import styles from "./styles/CourseItem.module.css";
import { Link } from "react-router-dom";
import { LECTURER } from "../constants/roles";
import { COURSE_DETAIL_PAGE, EDIT_COURSE_PAGE } from "../constants/paths";
import api from "../services/apiService";
import getErrorMessage from "../utils/error";

const CourseItem = ({ course, user, setError }) => {
	const [modalOpen, setModalOpen] = useState(false);

	const openModal = () => setModalOpen(true);
	const closeModal = () => setModalOpen(false);

	const onDeleteClick = async () => {
		try {
			await api.deleteCourse(course.code);
		} catch (error) {
			setError(getErrorMessage(error));
		} finally {
			setModalOpen(false);
		}
	};

	return (
		<div className={styles.courseItemContainer}>
			<div className={styles.courseTitleContainer}>
				<Link
					className={`${styles.courseTitle} ${styles.hyperlink}`}
					to={COURSE_DETAIL_PAGE.replace(":code", course.code)}
				>
					{course.code} - {course.name}
				</Link>

				{user?.role === LECTURER && (
					<div className={styles.buttonsContainer}>
						<Link to={EDIT_COURSE_PAGE.replace(":code", course.code)}>
							<button className={styles.baseButton}>Edit</button>
						</Link>
						<button
							className={styles.baseButton}
							onClick={openModal}
						>
							Delete
						</button>
					</div>
				)}
			</div>

			<p className={styles.courseDescription}>{course.description}</p>

			<div className={styles.academicUsers}>
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
			{modalOpen && (
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
									onClick={onDeleteClick}
								>
									Delete
								</button>
							</div>
						</div>
					</div>
				</div>
			)}
		</div>
	);
};

export default CourseItem;
