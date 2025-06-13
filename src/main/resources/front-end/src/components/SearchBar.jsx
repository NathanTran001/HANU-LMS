import styles from "./styles/SearchBar.module.css";

const SearchBar = ({
	value,
	onChange,
	onSubmit,
	placeholder = "Search...",
}) => (
	<form
		onSubmit={onSubmit}
		className={styles.searchForm}
	>
		<button
			type="submit"
			// tabIndex={-1}
			className={styles.searchBtn}
		>
			<i className="bi bi-search"></i>
		</button>
		<input
			className={styles.searchInput}
			type="text"
			value={value}
			onChange={onChange}
			placeholder={placeholder}
			autoComplete="off"
		/>
	</form>
);

export default SearchBar;
