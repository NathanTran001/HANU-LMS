import React, { useState } from "react";
import styles from "./styles/AdminOperationPage.module.css";
import { useNavigate } from "react-router-dom";
import api from "../services/apiService";
import { FACULTY_LIST_PAGE } from "../constants/paths";
import getErrorMessages from "../utils/error";

const CreateFacultyPage = () => {
	const [faculty, setFaculty] = useState({
		code: "",
		name: "",
	});
	const navigate = useNavigate();
	const [errors, setErrors] = useState([]);
	const [isSubmitting, setIsSubmitting] = useState(false);

	const handleInputChange = (e) => {
		const { name, value } = e.target;
		setFaculty((prev) => ({
			...prev,
			[name]: value,
		}));
	};

	const handleSubmit = async (e) => {
		e.preventDefault();
		setIsSubmitting(true);
		setErrors([]);

		try {
			await api.createFaculty(faculty);
			console.log("Faculty created successfully");
			setFaculty({ code: "", name: "" });
			navigate(FACULTY_LIST_PAGE);
		} catch (error) {
			setErrors(getErrorMessages(error));
		} finally {
			setIsSubmitting(false);
		}
	};

	return (
		<div className={styles.container}>
			<button
				type="button"
				className={styles.backBtn}
				onClick={() => navigate(-1)}
			>
				&#8592; Back
			</button>
			<div className={styles.titleContainer}>
				<h3 className={styles.title}>CREATE NEW FACULTY</h3>
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
						onChange={handleInputChange}
						type="text"
						className={styles.formControl}
						id="facultyCode"
						placeholder="Enter Faculty Code"
						required
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
				</div>
				{errors &&
					errors.map(
						(err, i) =>
							err && (
								<div
									key={i}
									className={styles.error}
								>
									{err}
								</div>
							)
					)}
				<button
					type="submit"
					className={styles.btn}
					disabled={isSubmitting}
				>
					{isSubmitting ? "Creating..." : "Create"}
				</button>
			</form>
		</div>
	);
};

export default CreateFacultyPage;
