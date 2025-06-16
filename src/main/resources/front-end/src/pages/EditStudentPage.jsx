import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import styles from "./styles/AdminOperationPage.module.css";
import api from "../services/apiService";
import { STUDENT_LIST_PAGE } from "../constants/paths";
import TextField from "../components/TextField";
import PasswordField from "../components/PasswordField";
import DropdownField from "../components/DropdownField";
import getErrorMessages from "../utils/error";

const EditStudentPage = () => {
	const { id } = useParams();
	const navigate = useNavigate();
	const [student, setStudent] = useState({
		id: "",
		name: "",
		facultyCode: "",
		email: "",
		username: "",
		password: "",
	});
	const [faculties, setFaculties] = useState([]);
	const [errors, setErrors] = useState([]);
	const [isSubmitting, setIsSubmitting] = useState(false);
	const [loading, setLoading] = useState(true);

	useEffect(() => {
		const fetchData = async () => {
			try {
				const [studentRes, facultiesRes] = await Promise.all([
					api.getStudent(id),
					api.getFaculties(0, 100),
				]);
				setStudent({
					...studentRes,
					facultyCode: studentRes.faculty?.code || "",
					password: "",
				});
				setFaculties(facultiesRes.content);
			} catch (error) {
				setErrors(getErrorMessages(error));
			} finally {
				setLoading(false);
			}
		};
		fetchData();
	}, [id]);

	const handleInputChange = (value, field) => {
		setStudent({ ...student, [field]: value });
	};

	const handleSubmit = async (e) => {
		e.preventDefault();
		setIsSubmitting(true);
		try {
			await api.updateStudent(id, student);
			navigate(STUDENT_LIST_PAGE);
		} catch (error) {
			setErrors(getErrorMessages(error));
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
				<h3 className={styles.title}>EDIT STUDENT</h3>
			</div>
			<form
				onSubmit={handleSubmit}
				className={styles.form}
			>
				<TextField
					label="ID"
					value={student.id}
					readonly
				/>
				<TextField
					label="Full Name"
					required
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
					value={student.email}
					onChange={(v) => handleInputChange(v, "email")}
					icon="envelope"
				/>
				<TextField
					label="Username"
					value={student.username}
					icon="person-badge"
					readonly
				/>
				<PasswordField
					label="Password"
					placeholder="Leave blank to keep unchanged"
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
					{isSubmitting ? "Updating..." : "Update"}
				</button>
			</form>
		</div>
	);
};

export default EditStudentPage;
