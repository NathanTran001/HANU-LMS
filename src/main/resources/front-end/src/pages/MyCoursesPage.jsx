import { useState, useEffect } from "react";
import styles from "./styles/MyCoursesPage.module.css";
import api from "../services/apiService";
import { LECTURER } from "../constants/roles";
import { useAuth } from "../contexts/AuthContext";
import { Link } from "react-router-dom";
import { CREATE_COURSE_PAGE } from "../constants/paths";
import CourseItem from "../components/CourseItem";

const MyCoursesPage = () => {
	const [courses, setCourses] = useState([]);
	const [message, setMessage] = useState("");
	const [error, setError] = useState("");
	const { isAuthenticated, user, checkAuthStatus, loading } = useAuth();

	useEffect(() => {
		checkAuthStatus();

		fetchCourses();
	}, []);

	const fetchCourses = async () => {
		try {
			// Replace this with your actual API call
			// const response = await fetch('/api/courses');
			// const coursesData = await response.json();

			// Placeholder data for demonstration
			const coursesData = await api.getCourses();
			console.log("Courses fetched: " + coursesData.content);

			setCourses(coursesData.content);
		} catch (error) {
			console.error("Error fetching courses:", error);
			setMessage("Error loading courses");
		}
	};

	return (
		<div className={styles.containerLg}>
			<div className={`${styles.titleContainer} ${styles.mb3}`}>
				<h1 className={styles.title}>MY COURSES</h1>

				{user?.role === LECTURER && (
					<Link to={CREATE_COURSE_PAGE}>
						<button
							className={styles.baseButton}
							// onClick={onCreateClick}
						>
							Create
						</button>
					</Link>
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
			{error && <div className={styles.textError}>{error}</div>}
			{courses.map((course) => (
				<CourseItem
					key={course.code}
					course={course}
					user={user}
					setError={setError}
				/>
			))}
		</div>
	);
};

export default MyCoursesPage;
