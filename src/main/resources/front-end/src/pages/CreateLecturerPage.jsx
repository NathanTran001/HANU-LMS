import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import styles from "./styles/AdminOperationPage.module.css";
import api from "../services/apiService";
import { LECTURER_LIST_PAGE } from "../constants/paths";
import TextField from "../components/TextField";
import PasswordField from "../components/PasswordField";
import DropdownField from "../components/DropdownField";

const CreateLecturerPage = () => {
	const [lecturer, setLecturer] = useState({
		name: "",
		faculty: "",
		email: "",
		username: "",
		password: "",
	});
	const [faculties, setFaculties] = useState([]);
	const [errors, setErrors] = useState({});
	const [isSubmitting, setIsSubmitting] = useState(false);
	const navigate = useNavigate();

	useEffect(() => {
		const fetchFaculties = async () => {
			try {
				const res = await api.getAllFaculties();
				setFaculties(res.content);
			} catch {
				setFaculties([]);
			}
		};
		fetchFaculties();
	}, []);

	const handleInputChange = (value, field) => {
		setLecturer({ ...lecturer, [field]: value });
	};

	const validate = () => {
		const errs = {};
		if (!lecturer.name) errs.name = "Name is required";
		if (!lecturer.faculty) errs.faculty = "Faculty is required";
		if (!lecturer.email) errs.email = "Email is required";
		if (!lecturer.username) errs.username = "Username is required";
		if (!lecturer.password) errs.password = "Password is required";
		return errs;
	};

	const handleSubmit = async (e) => {
		e.preventDefault();
		const errs = validate();
		if (Object.keys(errs).length) {
			setErrors(errs);
			return;
		}
		setIsSubmitting(true);
		try {
			const selectedFaculty = faculties.find(
				(f) => f.code === lecturer.faculty
			);
			if (!selectedFaculty) {
				setErrors({ faculty: "Selected faculty not found." });
				setIsSubmitting(false);
				return;
			}
			const lecturerToSend = {
				...lecturer,
				faculty: selectedFaculty,
			};
			await api.createLecturer(lecturerToSend);
			navigate(LECTURER_LIST_PAGE);
		} catch (error) {
			setErrors({ form: "Failed to create academicUser. Please try again." });
		} finally {
			setIsSubmitting(false);
		}
	};

	return (
		<div className={styles.container}>
			<button
				type="button"
				className={styles.backBtn}
				onClick={() => navigate(LECTURER_LIST_PAGE)}
			>
				&#8592; Back
			</button>
			<div className={styles.titleContainer}>
				<h3 className={styles.title}>CREATE LECTURER</h3>
			</div>
			<form
				onSubmit={handleSubmit}
				className={styles.form}
			>
				<TextField
					label="Full Name"
					required
					placeholder="e.g. John Doe"
					value={lecturer.name}
					onChange={(v) => handleInputChange(v, "name")}
					icon="person"
				/>
				<DropdownField
					label="Faculty"
					required
					value={lecturer.faculty}
					onChange={(v) => handleInputChange(v, "faculty")}
					options={faculties.map((f) => ({
						value: f.code,
						label: `${f.code} - ${f.name}`,
					}))}
				/>
				<TextField
					label="Email"
					required
					placeholder="e.g. john@hanu.edu.vn"
					value={lecturer.email}
					onChange={(v) => handleInputChange(v, "email")}
					icon="envelope"
				/>
				<TextField
					label="Username"
					required
					placeholder="e.g. johndoe"
					value={lecturer.username}
					onChange={(v) => handleInputChange(v, "username")}
					icon="person-badge"
				/>
				<PasswordField
					label="Password"
					required
					placeholder="Enter password"
					value={lecturer.password}
					onChange={(v) => handleInputChange(v, "password")}
				/>
				{Object.values(errors).map(
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

export default CreateLecturerPage;
