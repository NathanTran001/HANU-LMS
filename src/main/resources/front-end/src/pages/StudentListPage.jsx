import { useState, useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import ConfirmModal from "../components/ConfirmModal";
import styles from "./styles/AdminObservationPage.module.css";
import api from "../services/apiService";
import {
	CREATE_STUDENT_PAGE,
	EDIT_STUDENT_PAGE,
	LOGIN_PAGE,
} from "../constants/paths";
import SearchBarAdmin from "../components/SearchBarAdmin";
import Pagination from "../components/Pagination";
import DataTableAdmin from "../components/DataTableAdmin";

const StudentListPage = () => {
	const navigate = useNavigate();
	const [students, setStudents] = useState([]);
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState("");
	const [searchPhrase, setSearchPhrase] = useState("");
	const [deleteModal, setDeleteModal] = useState({
		isOpen: false,
		student: null,
	});
	const [page, setPage] = useState(0);
	const [size, setSize] = useState(3);
	const [totalPages, setTotalPages] = useState(0);
	const debounceTimeout = useRef();

	useEffect(() => {
		if (searchPhrase.trim()) {
			performSearch();
		} else {
			fetchStudents();
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
			}
		}, 400);

		return () => clearTimeout(debounceTimeout.current);
	}, [searchPhrase]);

	const fetchStudents = async () => {
		try {
			setLoading(true);
			const response = await api.getStudents(page, size);
			setStudents(response.content);
			console.log("Fetched students:", response.content);

			setTotalPages(response.totalPages);
		} catch (error) {
			console.error("Error fetching students:", error);
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
			const response = await api.searchStudents(searchPhrase, page, size);
			setStudents(response.content);
			setTotalPages(response.totalPages);
		} catch (error) {
			console.error("Error searching students:", error);
			setError("Failed to search students. Please try again.");
			setStudents([]);
			setTotalPages(0);
		} finally {
			setLoading(false);
		}
	};

	const handleSearch = async (e) => {
		if (e) e.preventDefault();

		if (!searchPhrase.trim()) {
			fetchStudents();
			return;
		}

		setPage(0);
		performSearch();
	};

	const handleCreateStudent = () => {
		navigate(CREATE_STUDENT_PAGE);
	};

	const handleEditStudent = (studentId) => {
		navigate(EDIT_STUDENT_PAGE.replace(":id", studentId));
	};

	const handleDeleteClick = (student) => {
		setDeleteModal({
			isOpen: true,
			student: student,
		});
	};

	const handleDeleteConfirm = async () => {
		if (!deleteModal.student) return;
		try {
			await api.deleteStudent(deleteModal.student.id);
			setDeleteModal({ isOpen: false, student: null });

			if (searchPhrase.trim()) {
				performSearch();
			} else {
				fetchStudents();
			}
		} catch (error) {
			console.error("Error deleting student:", error);
			setError("Failed to delete student. Please try again.");
		}
	};

	const handleDeleteCancel = () => {
		setDeleteModal({ isOpen: false, student: null });
	};

	return (
		<div className={styles.container}>
			<div className={styles.titleContainer}>
				<h3 className={styles.title}>STUDENT LIST</h3>
			</div>
			<div className={styles.actionsRow}>
				<SearchBarAdmin
					value={searchPhrase}
					onChange={(e) => setSearchPhrase(e.target.value)}
					onSubmit={handleSearch}
				/>
				<div className={styles.createBtnContainer}>
					<button
						onClick={handleCreateStudent}
						className={styles.createBtn}
						type="button"
					>
						Create
					</button>
				</div>
			</div>
			{error && <div className={styles.error}>{error}</div>}
			<div className={styles.tableContainer}>
				<DataTableAdmin
					columns={[
						{ key: "id", label: "ID" },
						{ key: "name", label: "Name" },
						{
							key: "faculty",
							label: "Faculty",
							render: (student) => student.facultyCode || "",
						},
						{ key: "email", label: "Email" },
						{ key: "username", label: "Username" },
					]}
					data={students}
					loading={loading}
					noDataText="Loading students..."
					onEdit={handleEditStudent}
					onDelete={handleDeleteClick}
				/>
			</div>
			{!loading && students.length > 0 && (
				<Pagination
					page={page}
					totalPages={totalPages}
					onPageChange={setPage}
				/>
			)}
			<ConfirmModal
				isOpen={deleteModal.isOpen}
				title="Confirm Student Deletion"
				message="Are you sure you want to delete this student?"
				onConfirm={handleDeleteConfirm}
				onCancel={handleDeleteCancel}
				confirmText="Delete Student"
				cancelText="Cancel"
				confirmButtonClass={styles.modalDeleteBtn}
			/>
		</div>
	);
};

export default StudentListPage;
