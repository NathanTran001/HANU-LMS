import React, { useState } from "react";
import styles from "./styles/CreateFacultyPage.module.css";
import { useNavigate } from "react-router-dom";
import api from "../services/apiService";

const CreateFacultyPage = () => {
	const [faculty, setFaculty] = useState({
		code: "",
		name: "",
	});
	const navigate = useNavigate();
	const [errors, setErrors] = useState({});
	const [isSubmitting, setIsSubmitting] = useState(false);

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
			api.createFaculty(faculty);
			console.log("Faculty created successfully");
			setFaculty({ code: "", name: "" });
			navigate("/admin/listFaculty");
		} catch (error) {
			if (error.response && error.response.data) {
				setErrors(error.response.data.errors || {});
			} else {
				console.error("Error creating faculty:", error);
			}
		} finally {
			setIsSubmitting(false);
		}
	};

	return (
		<div className={`${styles.listAccounts} container`}>
			<div className={`row ${styles.pathContainer} mt-3`}>
				<a
					href=""
					className={styles.path}
				>
					Home
				</a>{" "}
				/
				<a
					href=""
					className={styles.path}
				>
					Accounts Management
				</a>{" "}
				/
				<a
					href=""
					className={styles.path}
				>
					Create new Faculty
				</a>
			</div>

			<div className={`row ${styles.titleContainer} my-2`}>
				<h3 className={styles.title}>CREATE NEW FACULTY</h3>
			</div>

			<form onSubmit={handleSubmit}>
				<div className="row mb-3">
					<label
						htmlFor="facultyCode"
						className="form-label"
					>
						Faculty Code
					</label>
					<input
						name="code"
						value={faculty.code}
						onChange={handleInputChange}
						type="text"
						className="form-control"
						id="facultyCode"
						placeholder="Enter Faculty Code"
						required
					/>
					{errors.code && <div className={styles.error}>{errors.code}</div>}
				</div>

				<div className="row mb-3">
					<label
						htmlFor="facultyName"
						className="form-label"
					>
						Faculty Name
					</label>
					<input
						name="name"
						value={faculty.name}
						onChange={handleInputChange}
						type="text"
						className="form-control"
						id="facultyName"
						placeholder="Enter Faculty Name"
						required
					/>
					{errors.name && <div className={styles.error}>{errors.name}</div>}
				</div>

				<div className="row mb-3">
					<button
						type="submit"
						className={`btn btn-outline-secondary mb-3 px-5 py-2 ${styles.btn}`}
						disabled={isSubmitting}
					>
						{isSubmitting ? "Creating..." : "Create"}
					</button>
				</div>
			</form>
		</div>
	);
};

export default CreateFacultyPage;
