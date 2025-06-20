import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import styles from "./styles/LecturerOperationPage.module.css";
import api from "../services/apiService";
import { MY_COURSES_PAGE } from "../constants/paths";
import TextField from "../components/TextField";
import DropdownField from "../components/DropdownField";
import MultiSelectLecturers from "../components/MultiSelectLecturers";
import getErrorMessages from "../utils/error";

const EditCoursePage = () => {
	const { code } = useParams();
	const [course, setCourse] = useState(null);
	const [faculties, setFaculties] = useState([]);
	const [lecturers, setLecturers] = useState([]);
	const [errors, setErrors] = useState([]);
	const [isSubmitting, setIsSubmitting] = useState(false);
	const navigate = useNavigate();

	useEffect(() => {
		const fetchCourse = async () => {
			try {
				const data = await api.getCourse(code);
				setCourse({
					code: data.code || "",
					name: data.name || "",
					enrolmentKey: data.enrolmentKey || "",
					description: data.description || "",
					facultyCode: data.facultyCode || "",
					lecturerIds: data.lecturers ? data.lecturers.map((l) => l.id) : [],
				});
			} catch (error) {
				setErrors(getErrorMessages(error));
			}
		};
		const fetchFaculties = async () => {
			try {
				const facs = await api.getAllFaculties();
				setFaculties(facs);
			} catch {
				setFaculties([]);
			}
		};
		fetchCourse();
		fetchFaculties();
	}, [code]);

	useEffect(() => {
		const fetchLecturers = async () => {
			if (course && course.facultyCode) {
				try {
					const lecs = await api.getAllLecturers(course.facultyCode);
					setLecturers(lecs);
				} catch (error) {
					setErrors(getErrorMessages(error));
					setLecturers([]);
				}
			} else {
				setLecturers([]);
			}
		};
		fetchLecturers();
	}, [course?.facultyCode]);

	const handleInputChange = (value, field) => {
		if (field === "facultyCode") {
			setCourse((prevCourse) => ({
				...prevCourse,
				lecturerIds: [],
			}));
		}
		setCourse((prev) => ({ ...prev, [field]: value }));
	};

	const handleSubmit = async (e) => {
		e.preventDefault();
		setIsSubmitting(true);
		try {
			await api.updateCourse(course.code, course);
			navigate(MY_COURSES_PAGE);
		} catch (error) {
			setErrors(getErrorMessages(error));
		} finally {
			setIsSubmitting(false);
		}
	};

	if (!course) {
		return (
			<div className={styles.container}>
				<div>Loading course data...</div>
			</div>
		);
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
				<h3 className={styles.title}>EDIT COURSE</h3>
			</div>
			<form
				onSubmit={handleSubmit}
				className={styles.form}
			>
				<div className={styles.row}>
					<div className={styles.col7}>
						<TextField
							label="Course Code"
							required
							placeholder="e.g. 61FIT3SE2"
							value={course.code}
							onChange={(v) => handleInputChange(v, "code")}
							icon="hash"
							readonly={true}
						/>
						<TextField
							label="Course Name"
							required
							placeholder="e.g. Software Engineering 2"
							value={course.name}
							onChange={(v) => handleInputChange(v, "name")}
							icon="book-half"
						/>
						<TextField
							label="Enrolment Key"
							required
							placeholder="Enter enrolment key"
							value={course.enrolmentKey}
							onChange={(v) => handleInputChange(v, "enrolmentKey")}
							icon="key-fill"
						/>
						<div>
							<MultiSelectLecturers
								label={"Select Lecturers"}
								lecturers={lecturers}
								selected={course.lecturerIds}
								onChange={(ids) => handleInputChange(ids, "lecturerIds")}
								required
							/>
						</div>
						<div>
							<TextField
								label="Course Description"
								multiline={true}
								className={styles.formControl}
								style={{ height: 140 }}
								required
								placeholder="e.g. Software Engineering 2 is..."
								value={course.description}
								onChange={(e) => handleInputChange(e, "description")}
							/>
							<div className={styles.buttonGroup}>
								<button
									type="button"
									className={styles.cancelButton}
									onClick={() => navigate(MY_COURSES_PAGE)}
								>
									Cancel
								</button>
								<button
									type="submit"
									className={styles.btn}
									disabled={isSubmitting}
								>
									{isSubmitting ? "Saving..." : "Save"}
								</button>
							</div>
						</div>
					</div>
					<div className={styles.col3}>
						<DropdownField
							placeholder="Select Faculty Code"
							label="Faculty Code"
							required
							value={course.facultyCode}
							onChange={(v) => handleInputChange(v, "facultyCode")}
							options={[
								...faculties.map((f) => ({
									value: f.code,
									label: f.code,
								})),
							]}
							icon={"alphabet-uppercase"}
						/>
					</div>
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
			</form>
		</div>
	);
};

export default EditCoursePage;
