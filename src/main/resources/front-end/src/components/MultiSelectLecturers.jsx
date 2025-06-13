import styles from "../pages/styles/LecturerOperationPage.module.css";
import { useState } from "react";

// MultiSelectLecturers component for selecting multiple lecturers
export default function MultiSelectLecturers({
	lecturers,
	selected,
	onChange,
	error,
	label,
	required = false,
	icon = "person-fill",
}) {
	const [search, setSearch] = useState("");
	const [open, setOpen] = useState(false);

	const filtered = lecturers.filter(
		(l) =>
			l.name.toLowerCase().includes(search.toLowerCase()) ||
			l.email?.toLowerCase().includes(search.toLowerCase())
	);

	const toggleLecturer = (id) => {
		if (selected.includes(id)) {
			onChange(selected.filter((lid) => lid !== id));
		} else {
			onChange([...selected, id]);
		}
	};

	const selectAll = () => {
		if (selected.length === lecturers.length) {
			onChange([]);
		} else {
			onChange(lecturers.map((l) => l.id));
		}
	};

	return (
		<div className={styles.msLabelContainer}>
			{label && (
				<label className={styles.msLabel}>
					{label}
					{required && <span className={styles.msRequired}>*</span>}
				</label>
			)}
			<div className={styles.msInputWrapper}>
				<div className={styles.msInputInnerWrapper}>
					{icon && (
						<span className={styles.textfieldIconBox}>
							<i className={`bi bi-${icon} ${styles.textfieldIcon}`}></i>
						</span>
					)}
					<div
						className={styles.customSelect + (open ? " " + styles.open : "")}
					>
						<div
							className={styles.selectBox}
							onClick={() => setOpen((v) => !v)}
							tabIndex={0}
						>
							<div className={styles.selectedOptions}>
								{selected.length === 0 ? (
									<span className={styles.placeholder}>Select lecturers</span>
								) : (
									<>
										{lecturers
											.filter((l) => selected.includes(l.id))
											.slice(0, 3)
											.map((l) => (
												<span
													className={styles.tag}
													key={l.id}
												>
													{l.name}
													<span
														className={styles.removeTag}
														onClick={(e) => {
															e.stopPropagation();
															toggleLecturer(l.id);
														}}
													>
														&times;
													</span>
												</span>
											))}
										{selected.length > 3 && (
											<span className={styles.tag}>+{selected.length - 3}</span>
										)}
									</>
								)}
							</div>
							<div className={styles.arrow}>&#9662;</div>
						</div>
						{open && (
							<div className={styles.options}>
								<div className={styles.optionSearchTags}>
									<input
										type="text"
										className={styles.searchTags}
										placeholder="Search lecturers..."
										value={search}
										onChange={(e) => setSearch(e.target.value)}
									/>
									<button
										type="button"
										className={styles.clear}
										onClick={() => setSearch("")}
									>
										&times;
									</button>
								</div>
								<div
									className={
										styles.option +
										" " +
										styles.allTags +
										(selected.length === lecturers.length
											? " " + styles.active
											: "")
									}
									onClick={selectAll}
								>
									Select All
								</div>
								{filtered.length === 0 && (
									<div className={styles.noResultMessage}>
										No matched results
									</div>
								)}
								{filtered.map((l) => (
									<div
										key={l.id}
										className={
											styles.option +
											(selected.includes(l.id) ? " " + styles.active : "")
										}
										onClick={() => toggleLecturer(l.id)}
									>
										{l.name}{" "}
										{l.email && (
											<span className={styles.email}>({l.email})</span>
										)}
									</div>
								))}
							</div>
						)}
					</div>
				</div>
			</div>
		</div>
	);
}
