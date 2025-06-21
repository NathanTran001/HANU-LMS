import React, { useState } from "react";
import styles from "./styles/TextField.module.css";

const PasswordField = ({
	label,
	placeholder = "",
	required = false,
	value,
	onChange,
	readonly = false,
}) => {
	// const [show, setShow] = useState(false);

	return (
		<div className={styles.textfieldContainer}>
			<label className={styles.textfieldLabel}>
				{label}
				{required && <span className={styles.textfieldRequired}>*</span>}
			</label>
			<div className={styles.textfieldInputWrapper}>
				<input
					type={"password"}
					placeholder={placeholder}
					required={required}
					value={value}
					onChange={(e) => onChange(e.target.value)}
					className={styles.textfieldInput}
					readOnly={readonly}
				/>
				{/* <button
					type="button"
					className={styles.textfieldIconBox}
					onClick={() => setShow((s) => !s)}
					tabIndex={-1}
					style={{ border: "none", background: "none" }}
				>
					<i className={`bi ${show ? "bi-eye-slash" : "bi-eye"}`}></i>
				</button> */}
			</div>
		</div>
	);
};

export default PasswordField;
