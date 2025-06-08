import React from "react";
import styles from "./styles/TextField.module.css";

const TextField = ({
	label,
	icon,
	placeholder = "",
	required = false,
	value,
	onChange,
	multiline = false,
	readonly = false,
}) => {
	const InputComponent = multiline ? "textarea" : "input";

	return (
		<div className={styles.textfieldContainer}>
			<label className={styles.textfieldLabel}>
				{label}
				{required && <span className={styles.textfieldRequired}>*</span>}
			</label>
			<div className={styles.textfieldInputWrapper}>
				{icon && (
					<div className={styles.textfieldIconBox}>
						<i className={`bi bi-${icon} ${styles.textfieldIcon}`}></i>
					</div>
				)}
				<InputComponent
					type={multiline ? undefined : "text"}
					placeholder={placeholder}
					required={required}
					value={value}
					onChange={(e) => onChange(e.target.value)}
					className={`${styles.textfieldInput} ${
						multiline ? styles.textfieldTextarea : ""
					}`}
					rows={multiline ? 4 : undefined}
					readOnly={readonly}
				/>
			</div>
		</div>
	);
};

export default TextField;
