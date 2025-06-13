import { useState, useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import ConfirmModal from "../components/ConfirmModal";
import styles from "./styles/AdminObservationPage.module.css";
import api from "../services/apiService";
import {
	CREATE_FACULTY_PAGE,
	EDIT_FACULTY_PAGE,
	FACULTY_LIST_PAGE,
	LOGIN_PAGE,
} from "../constants/paths";
import SearchBar from "../components/SearchBar";
import Pagination from "../components/Pagination";
import DataTableAdmin from "../components/DataTableAdmin";
import { LECTURER, STUDENT } from "../constants/roles";

const FacultyListPage = () => {
	const navigate = useNavigate();
	const [faculties, setFaculties] = useState([]);
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState("");
	const [searchPhrase, setSearchPhrase] = useState("");
	const [deleteModal, setDeleteModal] = useState({
		isOpen: false,
		faculty: null,
	});
	const [page, setPage] = useState(0);
	const [pageSize, setPageSize] = useState(3);
	const [totalPages, setTotalPages] = useState(0);
	const debounceTimeout = useRef();

	useEffect(() => {
		if (searchPhrase.trim()) {
			performSearch();
		} else {
			fetchFaculties();
		}
	}, [page]);

	useEffect(() => {
		if (debounceTimeout.current) clearTimeout(debounceTimeout.current);

		debounceTimeout.current = setTimeout(() => {
			if (searchPhrase.trim()) {
				setPage(0);
				performSearch();
			} else {
				setPage(0);
				fetchFaculties();
			}
		}, 400);

		return () => clearTimeout(debounceTimeout.current);
	}, [searchPhrase]);

	const fetchFaculties = async () => {
		try {
			setLoading(true);
			const response = await api.getFaculties(page, pageSize);
			setFaculties(response.content);
			console.log("Fetched faculties:", response.content);
			console.log("Fetched faculties data:", response.data);

			setTotalPages(response.totalPages);
		} catch (error) {
			console.error("Error fetching faculties:", error);

			if (error.response && error.response.status === 403) {
				setError("You are not authorized to access this resource (403)");
				navigate(LOGIN_PAGE);
			} else {
				setError("Network error occurred");
			}
		} finally {
			setLoading(false);
		}
	};

	const performSearch = async () => {
		if (!searchPhrase.trim()) return;

		try {
			setLoading(true);
			setError("");
			const response = await api.searchFaculties(searchPhrase, page, pageSize);
			setFaculties(response.content);
			setTotalPages(response.totalPages);
		} catch (error) {
			console.error("Error searching faculties:", error);
			setError("Failed to search faculties. Please try again.");
			setFaculties([]);
			setTotalPages(0);
		} finally {
			setLoading(false);
		}
	};

	const handleSearch = async (e) => {
		if (e) e.preventDefault();

		if (!searchPhrase.trim()) {
			fetchFaculties();
			return;
		}

		setPage(0);
		performSearch();
	};

	const handleCreateFaculty = () => {
		navigate(CREATE_FACULTY_PAGE);
	};

	const handleEditFaculty = (facultyCode) => {
		navigate(FACULTY_LIST_PAGE.concat(`/edit/${facultyCode}`));
	};

	const handleDeleteClick = (faculty) => {
		setDeleteModal({
			isOpen: true,
			faculty: faculty,
		});
	};

	const handleDeleteConfirm = async () => {
		if (!deleteModal.faculty) return;
		try {
			await api.deleteFaculty(deleteModal.faculty.code);
			setDeleteModal({ isOpen: false, faculty: null });

			// Should refresh based on current state
			if (searchPhrase.trim()) {
				performSearch(); // If searching, refresh search results
			} else {
				fetchFaculties(); // If browsing, refresh browse results
			}
		} catch (error) {
			console.error("Error deleting faculty:", error);
			setError("Failed to delete faculty. Please try again.");
		}
	};

	const handleDeleteCancel = () => {
		setDeleteModal({ isOpen: false, faculty: null });
	};

	return (
		<div className={styles.container}>
			{/* Title */}
			<div className={styles.titleContainer}>
				<h3 className={styles.title}>FACULTY LIST</h3>
			</div>

			{/* Search and Create Section */}
			<div className={styles.actionsRow}>
				<SearchBar
					value={searchPhrase}
					onChange={(e) => setSearchPhrase(e.target.value)}
					onSubmit={handleSearch}
				/>
				<div className={styles.createBtnContainer}>
					<button
						onClick={handleCreateFaculty}
						className={styles.createBtn}
						type="button"
					>
						Create
					</button>
				</div>
			</div>

			{/* Error Display */}
			{error && <div className={styles.error}>{error}</div>}

			{/* Faculty Table */}
			<div className={styles.tableContainer}>
				<DataTableAdmin
					columns={[
						{ key: "code", label: "Code" },
						{ key: "name", label: "Name" },
						{
							key: "lecturers",
							label: "Lecturers",
							render: (row) => {
								return row.lecturerCount || 0;
							},
						},
						{
							key: "students",
							label: "Students",
							render: (row) => {
								return row.studentCount || 0;
							},
						},
						{
							key: "courses",
							label: "Courses",
							render: (row) => row.courseCount || 0,
						},
					]}
					data={faculties}
					loading={loading}
					noDataText="Loading faculties..."
					onEdit={handleEditFaculty}
					onDelete={handleDeleteClick}
				/>
			</div>
			{!loading && faculties.length > 0 && (
				<Pagination
					page={page}
					totalPages={totalPages}
					onPageChange={setPage}
				/>
			)}
			{/* Delete Confirmation Modal */}
			<ConfirmModal
				isOpen={deleteModal.isOpen}
				title="Confirm Faculty Deletion"
				message="Are you sure you want to delete this faculty along with all associated academicUsers, students, and courses?"
				onConfirm={handleDeleteConfirm}
				onCancel={handleDeleteCancel}
				confirmText="Delete Faculty"
				cancelText="Cancel"
				confirmButtonClass={styles.modalDeleteBtn}
			/>
		</div>
	);
};

export default FacultyListPage;
