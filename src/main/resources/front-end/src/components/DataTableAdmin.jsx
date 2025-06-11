import React from "react";
import styles from "./styles/DataTableAdmin.module.css";

const DataTableAdmin = ({
	columns,
	data,
	loading,
	noDataText,
	onEdit,
	onDelete,
}) => (
	<table className={styles.table}>
		<thead>
			<tr className={styles.tableHeader}>
				{columns.map((col) => (
					<th key={col.key}>{col.label}</th>
				))}
				<th>Edit</th>
				<th>Delete</th>
			</tr>
		</thead>
		<tbody>
			{loading ? (
				<tr className={styles.loadingRow}>
					<td colSpan={columns.length + 2}>{noDataText || "Loading..."}</td>
				</tr>
			) : data.length === 0 ? (
				<tr className={styles.noData}>
					<td colSpan={columns.length + 2}>No data found</td>
				</tr>
			) : (
				data.map((row) => (
					<tr
						key={row.id ?? row.code}
						className={styles.tableRow}
					>
						{columns.map((col) => (
							<td key={col.key}>
								{col.render ? col.render(row) : row[col.key]}
							</td>
						))}
						<td>
							<button
								className={styles.actionBtn}
								title="Edit"
								onClick={() => onEdit && onEdit(row.id ?? row.code)}
							>
								<i className="bi bi-pencil-square"></i>
							</button>
						</td>
						<td>
							<button
								className={`${styles.actionBtn} ${styles.deleteBtn}`}
								title="Delete"
								onClick={() => onDelete && onDelete(row)}
							>
								<i className="bi bi-trash3-fill"></i>
							</button>
						</td>
					</tr>
				))
			)}
		</tbody>
	</table>
);

export default DataTableAdmin;
