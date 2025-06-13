import { useState, useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import ConfirmModal from "../components/ConfirmModal";
import styles from "./styles/AdminObservationPage.module.css";
import api from "../services/apiService";
import {
	CREATE_LECTURER_PAGE,
	EDIT_LECTURER_PAGE,
	LOGIN_PAGE,
} from "../constants/paths";
import SearchBar from "../components/SearchBar";
import Pagination from "../components/Pagination";
import DataTableAdmin from "../components/DataTableAdmin";

const LecturerListPage = () => {
	const navigate = useNavigate();
	const [lecturers, setLecturers] = useState([]);
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState("");
	const [searchPhrase, setSearchPhrase] = useState("");
	const [deleteModal, setDeleteModal] = useState({
		isOpen: false,
		lecturer: null,
	});
	const [page, setPage] = useState(0);
	const [size, setSize] = useState(3);
	const [totalPages, setTotalPages] = useState(0);
	const debounceTimeout = useRef();

	useEffect(() => {
		if (searchPhrase.trim()) {
			performSearch();
		} else {
			fetchLecturers();
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
				fetchLecturers();
			}
		}, 400);

		return () => clearTimeout(debounceTimeout.current);
	}, [searchPhrase]);

	const fetchLecturers = async () => {
		try {
			setLoading(true);
			const response = await api.getLecturers(page, size);
			console.log("Fetched lecturers:", response.content);
			setLecturers(response.content);
			setTotalPages(response.totalPages);
		} catch (error) {
			console.error("Error fetching academicUsers:", error);
			if (error.response && error.response.status === 403) {
				setError("You are not authorized to access this resource (403)");
				// navigate(LOGIN_PAGE);
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
			const response = await api.searchLecturers(searchPhrase, page, size);
			setLecturers(response.content);
			setTotalPages(response.totalPages);
		} catch (error) {
			console.error("Error searching academicUsers:", error);
			setError("Failed to search academicUsers. Please try again.");
			setLecturers([]);
			setTotalPages(0);
		} finally {
			setLoading(false);
		}
	};

	const handleSearch = async (e) => {
		if (e) e.preventDefault();

		if (!searchPhrase.trim()) {
			fetchLecturers();
			return;
		}

		setPage(0);
		performSearch();
	};

	const handleCreateLecturer = () => {
		navigate(CREATE_LECTURER_PAGE);
	};

	const handleEditLecturer = (lecturerId) => {
		navigate(EDIT_LECTURER_PAGE.replace(":id", lecturerId));
	};

	const handleDeleteClick = (lecturer) => {
		setDeleteModal({
			isOpen: true,
			lecturer: lecturer,
		});
	};

	const handleDeleteConfirm = async () => {
		if (!deleteModal.lecturer) return;
		try {
			await api.deleteLecturer(deleteModal.lecturer.id);
			setDeleteModal({ isOpen: false, lecturer: null });

			if (searchPhrase.trim()) {
				performSearch();
			} else {
				fetchLecturers();
			}
		} catch (error) {
			console.error("Error deleting lecturer:", error);
			setError("Failed to delete lecturer. Please try again.");
		}
	};

	const handleDeleteCancel = () => {
		setDeleteModal({ isOpen: false, lecturer: null });
	};

	return (
		<div className={styles.container}>
			<div className={styles.titleContainer}>
				<h3 className={styles.title}>LECTURER LIST</h3>
			</div>
			<div className={styles.actionsRow}>
				<SearchBar
					value={searchPhrase}
					onChange={(e) => setSearchPhrase(e.target.value)}
					onSubmit={handleSearch}
				/>
				<div className={styles.createBtnContainer}>
					<button
						onClick={handleCreateLecturer}
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
							render: (lecturer) => lecturer.facultyCode || "",
						},
						{ key: "email", label: "Email" },
						{ key: "username", label: "Username" },
					]}
					data={lecturers}
					loading={loading}
					noDataText="Loading lecturers..."
					onEdit={handleEditLecturer}
					onDelete={handleDeleteClick}
				/>
			</div>
			{!loading && lecturers.length > 0 && (
				<Pagination
					page={page}
					totalPages={totalPages}
					onPageChange={setPage}
				/>
			)}
			<ConfirmModal
				isOpen={deleteModal.isOpen}
				title="Confirm Lecturer Deletion"
				message="Are you sure you want to delete this lecturer?"
				onConfirm={handleDeleteConfirm}
				onCancel={handleDeleteCancel}
				confirmText="Delete Lecturer"
				cancelText="Cancel"
				confirmButtonClass={styles.modalDeleteBtn}
			/>
		</div>
	);
};

export default LecturerListPage;
