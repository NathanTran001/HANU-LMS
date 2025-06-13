import React from "react";
import styles from "./styles/TextField.module.css";

const DropdownField = ({
	label,
	required = false,
	value,
	onChange,
	options = [],
	readonly = false,
	placeholder = "Select...",
	icon,
}) => (
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
			<select
				className={`${styles.textfieldInput} ${
					!value ? styles.placeholder : ""
				}`}
				value={value}
				onChange={(e) => onChange(e.target.value)}
				disabled={readonly}
				required={required}
			>
				<option
					value=""
					disabled
					className={styles.placeholderOption}
				>
					{placeholder}
				</option>
				{options.map((opt) => (
					<option
						key={opt.value}
						value={opt.value}
					>
						{opt.label}
					</option>
				))}
			</select>
		</div>
	</div>
);

export default DropdownField;
