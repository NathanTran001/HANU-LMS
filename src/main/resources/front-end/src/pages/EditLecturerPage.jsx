import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import styles from "./styles/AdminOperationPage.module.css";
import api from "../services/apiService";
import { LECTURER_LIST_PAGE } from "../constants/paths";
import TextField from "../components/TextField";
import PasswordField from "../components/PasswordField";
import DropdownField from "../components/DropdownField";

const EditLecturerPage = () => {
	const { id } = useParams();
	const navigate = useNavigate();
	const [academicUser, setLecturer] = useState({
		id: "",
		name: "",
		faculty: "", // store faculty code here
		email: "",
		username: "",
		password: "",
	});
	const [faculties, setFaculties] = useState([]);
	const [errors, setErrors] = useState({});
	const [isSubmitting, setIsSubmitting] = useState(false);
	const [loading, setLoading] = useState(true);

	useEffect(() => {
		const fetchData = async () => {
			try {
				const [lecturerRes, facultiesRes] = await Promise.all([
					api.getLecturer(id),
					api.getFaculties(0, 100),
				]);

				setLecturer({
					...lecturerRes,
					faculty: lecturerRes.faculty?.code || "", // store code only
					password: "",
				});
				setFaculties(facultiesRes.content); // store full objects
			} catch {
				setErrors({ form: "Failed to load data." });
			} finally {
				setLoading(false);
			}
		};
		fetchData();
	}, [id]);

	const handleInputChange = (value, field) => {
		setLecturer({ ...academicUser, [field]: value });
	};

	const validate = () => {
		const errs = {};
		if (!academicUser.name) errs.name = "Name is required";
		if (!academicUser.faculty) errs.faculty = "Faculty is required";
		if (!academicUser.email) errs.email = "Email is required";
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
			// Find the selected faculty object by code
			const selectedFaculty = faculties.find(
				(f) => f.code === academicUser.faculty
			);
			if (!selectedFaculty) {
				setErrors({ faculty: "Selected faculty not found." });
				setIsSubmitting(false);
				return;
			}
			const lecturerToSend = {
				...academicUser,
				faculty: selectedFaculty,
			};
			await api.updateLecturer(id, lecturerToSend);
			navigate(LECTURER_LIST_PAGE);
		} catch (error) {
			setErrors({ form: "Failed to update academicUser. Please try again." });
		} finally {
			setIsSubmitting(false);
		}
	};

	if (loading) return <div className={styles.container}>Loading...</div>;

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
				<h3 className={styles.title}>EDIT LECTURER</h3>
			</div>
			<form
				onSubmit={handleSubmit}
				className={styles.form}
			>
				<TextField
					label="ID"
					value={academicUser.id}
					readonly
				/>
				<TextField
					label="Full Name"
					required
					value={academicUser.name}
					onChange={(v) => handleInputChange(v, "name")}
					icon="person"
				/>
				<DropdownField
					label="Faculty"
					required
					value={academicUser.faculty}
					onChange={(v) => handleInputChange(v, "faculty")}
					options={faculties.map((f) => ({
						value: f.code,
						label: `${f.code} - ${f.name}`,
					}))}
				/>
				<TextField
					label="Email"
					required
					value={academicUser.email}
					onChange={(v) => handleInputChange(v, "email")}
					icon="envelope"
				/>
				<TextField
					label="Username"
					value={academicUser.username}
					icon="person-badge"
					readonly
				/>
				<PasswordField
					label="Password"
					placeholder="Leave blank to keep unchanged"
					value={academicUser.password}
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
					{isSubmitting ? "Updating..." : "Update"}
				</button>
			</form>
		</div>
	);
};

export default EditLecturerPage;
