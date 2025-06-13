import styles from "./styles/CourseItem.module.css";
import { Link } from "react-router-dom";
import { LECTURER } from "../constants/roles";

const CourseItem = ({
	course,
	user,
	activeModal,
	openModal,
	closeModal,
	onEditClick,
	onDeleteClick,
}) => (
	<div className={styles.courseItemContainer}>
		<div className={styles.courseTitleContainer}>
			<Link
				className={`${styles.courseTitle} ${styles.hyperlink}`}
				to={`/myCourses/course-details/${course.code}`}
			>
				{course.code} - {course.name}
			</Link>

			{user?.role === LECTURER && (
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
);

export default CourseItem;
