import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import styles from "./styles/AdminOperationPage.module.css";
import api from "../services/apiService";
import { STUDENT_LIST_PAGE } from "../constants/paths";
import TextField from "../components/TextField";
import PasswordField from "../components/PasswordField";
import DropdownField from "../components/DropdownField";
import getErrorMessage from "../utils/error";

const CreateStudentPage = () => {
	const [student, setStudent] = useState({
		name: "",
		facultyCode: "",
		email: "",
		username: "",
		password: "",
	});
	const [faculties, setFaculties] = useState([]);
	const [errors, setErrors] = useState([]);
	const [isSubmitting, setIsSubmitting] = useState(false);
	const navigate = useNavigate();

	useEffect(() => {
		const fetchFaculties = async () => {
			try {
				const res = await api.getAllFaculties();
				setFaculties(res);
			} catch {
				setFaculties([]);
			}
		};
		fetchFaculties();
	}, []);

	const handleInputChange = (value, field) => {
		setStudent({ ...student, [field]: value });
	};

	const handleSubmit = async (e) => {
		e.preventDefault();
		setIsSubmitting(true);
		try {
			await api.createStudent(student);
			navigate(STUDENT_LIST_PAGE);
		} catch (error) {
			setErrors(getErrorMessage(error));
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
				<h3 className={styles.title}>CREATE STUDENT</h3>
			</div>
			<form
				onSubmit={handleSubmit}
				className={styles.form}
			>
				<TextField
					label="Full Name"
					required
					placeholder="e.g. Jane Doe"
					value={student.name}
					onChange={(v) => handleInputChange(v, "name")}
					icon="person"
				/>
				<DropdownField
					label="Faculty"
					required
					value={student.facultyCode}
					onChange={(v) => handleInputChange(v, "facultyCode")}
					options={faculties.map((f) => ({
						value: f.code,
						label: `${f.code} - ${f.name}`,
					}))}
				/>
				<TextField
					label="Email"
					required
					placeholder="e.g. jane@hanu.edu.vn"
					value={student.email}
					onChange={(v) => handleInputChange(v, "email")}
					icon="envelope"
				/>
				<TextField
					label="Username"
					required
					placeholder="e.g. janedoe"
					value={student.username}
					onChange={(v) => handleInputChange(v, "username")}
					icon="person-badge"
				/>
				<PasswordField
					label="Password"
					required
					placeholder="Enter password"
					value={student.password}
					onChange={(v) => handleInputChange(v, "password")}
				/>
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

export default CreateStudentPage;
