import { useState, useEffect } from "react";
import styles from "./styles/AdminOperationPage.module.css";
import { useNavigate, useParams } from "react-router-dom";
import api from "../services/apiService";
import { FACULTY_LIST_PAGE } from "../constants/paths";
import TextField from "../components/TextField";
import getErrorMessages from "../utils/error";

const EditFacultyPage = () => {
	const { code } = useParams();
	const navigate = useNavigate();
	const [faculty, setFaculty] = useState({
		code: "",
		name: "",
	});
	const [errors, setErrors] = useState([]);
	const [isSubmitting, setIsSubmitting] = useState(false);
	const [loading, setLoading] = useState(true);

	// Fetch faculty details on component mount
	useEffect(() => {
		const fetchFaculty = async () => {
			try {
				const response = await api.getFaculty(code);
				setFaculty(response);
			} catch (error) {
				setErrors(getErrorMessages(error));
				navigate(FACULTY_LIST_PAGE); // Redirect if faculty not found
			} finally {
				setLoading(false);
			}
		};

		fetchFaculty();
	}, [code, navigate]);

	const handleInputChange = (value, fieldName) => {
		setFaculty((prev) => ({
			...prev,
			[fieldName]: value,
		}));
	};

	const handleSubmit = async (e) => {
		e.preventDefault();
		setIsSubmitting(true);
		setErrors([]);

		try {
			await api.updateFaculty(code, faculty);
			navigate(FACULTY_LIST_PAGE);
		} catch (error) {
			setErrors(getErrorMessages(error));
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
				onClick={() => navigate(-1)}
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
				<TextField
					label="Faculty Code"
					required
					placeholder="e.g. FIT"
					value={faculty.code}
					onChange={(value) => handleInputChange(value, "code")}
					icon={"bi bi-alphabet-uppercase text-secondary"}
					readonly={true}
				/>
				<TextField
					label="Faculty Name"
					required
					placeholder="e.g. Faculty of Information Technology"
					value={faculty.name}
					onChange={(value) => handleInputChange(value, "name")}
					icon={"bi bi-hash text-secondary"}
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

export default EditFacultyPage;
