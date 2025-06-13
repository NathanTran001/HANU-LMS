import { useState, useEffect } from "react";
import { useAuth } from "../contexts/AuthContext";
import { LECTURER } from "../constants/roles";
import { Link, useParams } from "react-router-dom";
import api from "../services/apiService";
import CourseItem from "../components/CourseItem";
import SearchBar from "../components/SearchBar";
import Pagination from "../components/Pagination";
import styles from "./styles/CourseSearchPage.module.css";
import { CREATE_COURSE_PAGE } from "../constants/paths";

const SearchCoursePage = () => {
	const { user } = useAuth();
	const { searchPhrase } = useParams();
	const [searchValue, setSearchValue] = useState(searchPhrase || "");
	const [courses, setCourses] = useState([]);
	const [message, setMessage] = useState("");
	const [activeModal, setActiveModal] = useState(null);
	const [loading, setLoading] = useState(false);
	const [page, setPage] = useState(0);
	const [pageSize] = useState(2);
	const [totalPages, setTotalPages] = useState(0);

	useEffect(() => {
		if (searchValue.trim()) {
			handleSearch();
		} else {
			setCourses([]);
			setMessage("");
		}
		// eslint-disable-next-line
	}, [searchValue, page]);

	const handleSearch = async (e) => {
		if (e) e.preventDefault();
		if (!searchValue.trim()) {
			setCourses([]);
			setMessage("");
			setTotalPages(0);
			return;
		}
		setLoading(true);
		try {
			const result = await api.searchCourse(searchValue.trim(), page, pageSize);
			setCourses(Array.isArray(result.content) ? result.content : []);
			setTotalPages(result.totalPages || 0);
			if (!result.content || result.content.length === 0) {
				setMessage("No courses found.");
			} else {
				setMessage("");
			}
		} catch (error) {
			setMessage("Error searching courses.");
			setCourses([]);
			setTotalPages(0);
		} finally {
			setLoading(false);
		}
	};

	const onEditClick = (courseCode) => {
		window.location.href = `/editCourse/${courseCode}`;
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

	return (
		<div className={styles.containerLg}>
			<div className={styles.titleContainer}>
				<h1 className={styles.title}>SEARCH COURSES</h1>
				{user?.role === LECTURER && (
					<Link to={CREATE_COURSE_PAGE}>
						<button className={styles.baseButton}>Create</button>
					</Link>
				)}
			</div>
			<div className={styles.searchBarContainer}>
				<SearchBar
					value={searchValue}
					onChange={(e) => {
						setSearchValue(e.target.value);
						setPage(0); // Reset to first page on new search
					}}
					onSubmit={handleSearch}
					placeholder="Search courses..."
				/>
			</div>
			{message && <div className={styles.textSuccess}>{message}</div>}
			{loading && <div className={styles.loading}>Loading...</div>}
			{courses.map((course) => (
				<CourseItem
					key={course.code}
					course={course}
					user={user}
					activeModal={activeModal}
					openModal={openModal}
					closeModal={closeModal}
					onEditClick={onEditClick}
					onDeleteClick={onDeleteClick}
				/>
			))}
			{!loading && courses.length > 0 && (
				<Pagination
					page={page}
					totalPages={totalPages}
					onPageChange={setPage}
				/>
			)}
		</div>
	);
};

export default SearchCoursePage;
