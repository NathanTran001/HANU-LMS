import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import styles from "./styles/LecturerOperationPage.module.css";
import api from "../services/apiService";
import { MY_COURSES_PAGE } from "../constants/paths";
import TextField from "../components/TextField";
import DropdownField from "../components/DropdownField";
import MultiSelectLecturers from "../components/MultiSelectLecturers";

const CreateCoursePage = () => {
	const [course, setCourse] = useState({
		code: "",
		name: "",
		enrolmentKey: "",
		description: "",
		facultyCode: "",
		lecturerIds: [],
	});
	const [faculties, setFaculties] = useState([]);
	const [lecturers, setLecturers] = useState([]);
	const [errors, setErrors] = useState({});
	const [isSubmitting, setIsSubmitting] = useState(false);
	const navigate = useNavigate();

	useEffect(() => {
		fetchData();
	}, []);

	useEffect(() => {
		if (course.facultyCode) {
			setCourse((prev) => ({
				...prev,
				lecturerIds: [], // Reset lecturers when faculty changes
			}));
			api
				.getAllLecturers(course.facultyCode)
				.then(setLecturers)
				.catch(() => setLecturers([]));
		} else {
			setLecturers([]);
		}
	}, [course.facultyCode]);

	const fetchData = async () => {
		try {
			const [facutiesRes, lecturersRes] = await Promise.all([
				api.getAllFaculties(),
				api.getAllLecturers(course.facultyCode),
			]);
			console.log("Faculties:", facutiesRes);
			console.log("Lecturers:", lecturersRes);

			setFaculties(facutiesRes);
			setLecturers(lecturersRes);
		} catch {
			setFaculties([]);
			setLecturers([]);
		}
	};

	const handleInputChange = (value, field) => {
		setCourse({ ...course, [field]: value });
	};

	const validate = () => {
		const errs = {};
		if (!course.code) errs.code = "Course code is required";
		if (!course.name) errs.name = "Course name is required";
		if (!course.enrolmentKey) errs.enrolmentKey = "Enrolment key is required";
		if (!course.facultyCode) errs.facultyCode = "Faculty is required";
		if (!course.description) errs.description = "Description is required";
		if (!course.lecturerIds || course.lecturerIds.length === 0)
			errs.lecturerIds = "At least one lecturer must be selected";
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
			// const selectedFaculty = faculties.find((f) => f.code === course.faculty);
			// const selectedLecturers = lecturers.filter((l) =>
			// 	course.lecturerIds.includes(l.id)
			// );
			// if (!selectedFaculty) {
			// 	setErrors({ faculty: "Selected faculty not found." });
			// 	setIsSubmitting(false);
			// 	return;
			// }
			// const courseToSend = {
			// 	...course,
			// 	faculty: selectedFaculty,
			// 	lecturerIds: selectedLecturers,
			// };
			await api.createCourse(course);
			navigate(MY_COURSES_PAGE);
		} catch (error) {
			setErrors({ form: "Failed to create course. Please try again." });
		} finally {
			setIsSubmitting(false);
		}
	};

	return (
		<div className={styles.container}>
			<button
				type="button"
				className={styles.backBtn}
				onClick={() => navigate(MY_COURSES_PAGE)}
			>
				&#8592; Back
			</button>
			<div className={styles.titleContainer}>
				<h3 className={styles.title}>CREATE COURSE</h3>
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
							error={errors.code}
						/>
						<TextField
							label="Course Name"
							required
							placeholder="e.g. Software Engineering 2"
							value={course.name}
							onChange={(v) => handleInputChange(v, "name")}
							icon="book-half"
							error={errors.name}
						/>
						<TextField
							label="Enrolment Key"
							required
							placeholder="Enter enrolment key"
							value={course.enrolmentKey}
							onChange={(v) => handleInputChange(v, "enrolmentKey")}
							icon="key-fill"
							error={errors.enrolmentKey}
						/>
						<div>
							{/* <label className={styles.formLabel}>Lecturers</label> */}
							<MultiSelectLecturers
								label={"Select Lecturers"}
								lecturers={lecturers}
								selected={course.lecturerIds}
								onChange={(ids) => handleInputChange(ids, "lecturerIds")}
								error={errors.lecturerIds}
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
							{errors.description && (
								<div className={styles.error}>{errors.description}</div>
							)}
							{errors.form && <div className={styles.error}>{errors.form}</div>}
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
									{isSubmitting ? "Creating..." : "Create"}
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
							error={errors.facultyCode}
						/>
					</div>
				</div>
			</form>
		</div>
	);
};

export default CreateCoursePage;
