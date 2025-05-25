import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Layout from "../components/Layout";
import ConfirmModal from "../components/ConfirmModal";
import styles from "./styles/FacultyListPage.module.css";
import api from "../services/apiService";
import { createFacultyPage } from "../App";

const FacultyListPage = () => {
	const [faculties, setFaculties] = useState([]);
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState("");
	const [searchTerm, setSearchTerm] = useState("");
	const [deleteModal, setDeleteModal] = useState({
		isOpen: false,
		faculty: null,
	});
	const navigate = useNavigate();

	useEffect(() => {
		fetchFaculties();
	}, []);

	const fetchFaculties = async () => {
		try {
			setLoading(true);
			const fetchedFaculties = await api.getFaculties();
			setFaculties(Array.isArray(fetchedFaculties) ? fetchedFaculties : []);
		} catch (error) {
			console.error("Error fetching faculties:", error);

			if (error.response && error.response.status === 403) {
				setError("You are not authorized to access this resource (403)");
				navigate("/");
			} else {
				setError("Network error occurred");
			}
		} finally {
			setLoading(false);
		}
	};

	const handleSearch = async (e) => {};

	const handleCreateFaculty = () => {
		navigate(createFacultyPage);
	};

	const handleEditFaculty = (facultyCode) => {
		navigate(`/admin/editFaculty/${facultyCode}`);
	};

	const handleDeleteClick = (faculty) => {
		setDeleteModal({
			isOpen: true,
			faculty: faculty,
		});
	};

	const handleDeleteConfirm = async () => {};

	const handleDeleteCancel = () => {
		setDeleteModal({ isOpen: false, faculty: null });
	};

	if (loading) {
		return (
			<Layout>
				<div className={styles.loading}>Loading faculties...</div>
			</Layout>
		);
	}

	return (
		<div className={styles.listAccounts}>
			{/* Breadcrumb */}
			<div className={styles.pathContainer}>
				<a
					href="#"
					className={styles.path}
					onClick={(e) => {
						e.preventDefault();
						navigate("/");
					}}
				>
					Home
				</a>
				<span> / </span>
				<a
					href="#"
					className={styles.path}
					onClick={(e) => e.preventDefault()}
				>
					Faculty List
				</a>
			</div>

			{/* Title */}
			<div className={styles.titleContainer}>
				<h3 className={styles.title}>FACULTY LIST</h3>
			</div>

			{/* Search and Create Section */}
			<div className={styles.actionsRow}>
				<form
					onSubmit={handleSearch}
					className={styles.searchForm}
				>
					<button
						type="submit"
						className={styles.searchBtn}
					>
						<i className="bi bi-search"></i>
					</button>
					<input
						type="text"
						className={styles.searchInput}
						placeholder="Search..."
						value={searchTerm}
						onChange={(e) => setSearchTerm(e.target.value)}
					/>
				</form>
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
				<table className={styles.table}>
					<thead>
						<tr className={styles.tableHeader}>
							<th>Code</th>
							<th>Name</th>
							<th>Lecturers</th>
							<th>Students</th>
							<th>Courses</th>
							<th>Update</th>
							<th>Delete</th>
						</tr>
					</thead>
					<tbody>
						{loading ? (
							<tr>
								<td
									colSpan="7"
									className={styles.loadingRow}
								>
									Loading faculties...
								</td>
							</tr>
						) : faculties.length === 0 ? (
							<tr>
								<td
									colSpan="7"
									className={styles.noData}
								>
									No faculties found
								</td>
							</tr>
						) : (
							faculties.map((faculty) => (
								<tr
									key={faculty.code}
									className={styles.tableRow}
								>
									<td>
										<p>{faculty.code}</p>
									</td>
									<td>
										<p>{faculty.name}</p>
									</td>
									<td>
										<p>{faculty.lecturers?.length || 0}</p>
									</td>
									<td>
										<p>{faculty.students?.length || 0}</p>
									</td>
									<td>
										<p>{faculty.courses?.length || 0}</p>
									</td>
									<td>
										<button
											onClick={() => handleEditFaculty(faculty.code)}
											className={styles.actionBtn}
											title="Edit Faculty"
										>
											<i className="bi bi-pencil-square"></i>
										</button>
									</td>
									<td>
										<button
											onClick={() => handleDeleteClick(faculty)}
											className={`${styles.actionBtn} ${styles.deleteBtn}`}
											title="Delete Faculty"
										>
											<i className="bi bi-trash3-fill"></i>
										</button>
									</td>
								</tr>
							))
						)}
					</tbody>
				</table>
			</div>

			{/* Delete Confirmation Modal */}
			<ConfirmModal
				isOpen={deleteModal.isOpen}
				title="Confirm Faculty Deletion"
				message="Are you sure you want to delete this faculty along with all associated lecturers, students, and courses?"
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
