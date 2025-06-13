// hooks/useSearch.js
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { SEARCH_COURSE_PAGE } from "../constants/paths";

export const useSearch = () => {
	const [searchValue, setSearchValue] = useState("Search...");
	const navigate = useNavigate();

	const handleSearchFocus = () => {
		if (searchValue === "Search...") {
			setSearchValue("");
		}
	};

	const handleSearchBlur = () => {
		if (searchValue === "") {
			setSearchValue("Search...");
		}
	};

	const handleSearchKeyPress = (event) => {
		if (event.key === "Enter") {
			const searchPhrase = searchValue.trim();
			if (searchPhrase === "" || searchPhrase === "Search...") {
				// navigate("/searchCourseEmpty");
			} else {
				navigate(
					SEARCH_COURSE_PAGE.replace(
						":searchPhrase",
						`${encodeURIComponent(searchPhrase)}`
					)
				);
			}
		}
	};

	return {
		searchValue,
		setSearchValue,
		handleSearchFocus,
		handleSearchBlur,
		handleSearchKeyPress,
	};
};
