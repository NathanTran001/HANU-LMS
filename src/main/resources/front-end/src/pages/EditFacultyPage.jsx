import React, { useState, useEffect } from "react";
import styles from "./styles/AdminOperationPage.module.css";
import { useNavigate, useParams } from "react-router-dom";
import api from "../services/apiService";
import { FACULTY_LIST_PAGE } from "../constants/paths";

const EditFacultyPage = () => {
	const { facultyCode } = useParams(); // Get faculty code from the URL
	const navigate = useNavigate();
	const [faculty, setFaculty] = useState({
		code: "",
		name: "",
	});
	const [errors, setErrors] = useState({});
	const [isSubmitting, setIsSubmitting] = useState(false);
	const [loading, setLoading] = useState(true);

	// Fetch faculty details on component mount
	useEffect(() => {
		const fetchFaculty = async () => {
			try {
				const response = await api.getFaculty(facultyCode);
				setFaculty(response);
			} catch (error) {
				console.error("Error fetching faculty details:", error);
				navigate(FACULTY_LIST_PAGE); // Redirect if faculty not found
			} finally {
				setLoading(false);
			}
		};

		fetchFaculty();
	}, [facultyCode, navigate]);

	const handleInputChange = (e) => {
		const { name, value } = e.target;
		setFaculty((prev) => ({
			...prev,
			[name]: value,
		}));

		// Clear error when user starts typing
		if (errors[name]) {
			setErrors((prev) => ({
				...prev,
				[name]: "",
			}));
		}
	};

	const handleSubmit = async (e) => {
		e.preventDefault();
		setIsSubmitting(true);
		setErrors({});

		try {
			await api.updateFaculty(facultyCode, { name: faculty.name });
			console.log("Faculty updated successfully");
			navigate(FACULTY_LIST_PAGE);
		} catch (error) {
			if (error.response && error.response.data) {
				setErrors(error.response.data.errors || {});
			} else {
				console.error("Error updating faculty:", error);
			}
		} finally {
			setIsSubmitting(false);
		}
	};

	if (loading) {
		return <div className={styles.container}>Loading faculty details...</div>;
	}

	return (
		<div className={styles.container}>
			<button
				type="button"
				className={styles.backBtn}
				onClick={() => navigate(FACULTY_LIST_PAGE)}
			>
				&#8592; Back
			</button>
			<div className={styles.titleContainer}>
				<h3 className={styles.title}>EDIT FACULTY</h3>
			</div>

			<form
				onSubmit={handleSubmit}
				className={styles.form}
			>
				<div className={styles.formGroup}>
					<label
						htmlFor="facultyCode"
						className={styles.formLabel}
					>
						Faculty Code
					</label>
					<input
						name="code"
						value={faculty.code}
						type="text"
						className={styles.formControl}
						id="facultyCode"
						placeholder="Faculty Code"
						disabled // Make the code field immutable
					/>
				</div>

				<div className={styles.formGroup}>
					<label
						htmlFor="facultyName"
						className={styles.formLabel}
					>
						Faculty Name
					</label>
					<input
						name="name"
						value={faculty.name}
						onChange={handleInputChange}
						type="text"
						className={styles.formControl}
						id="facultyName"
						placeholder="Enter Faculty Name"
						required
					/>
					{errors.name && <div className={styles.error}>{errors.name}</div>}
				</div>

				<button
					type="submit"
					className={styles.btn}
					disabled={isSubmitting}
				>
					{isSubmitting ? "Updating..." : "Update"}
				</button>
			</form>
		</div>
	);
};

export default EditFacultyPage;
