// hooks/useNavBar.js
import { useState, useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { getUser, logout } from "../utils/auth";
import { loginPage } from "../App";

export const useNavBar = () => {
	const [isDropdownOpen, setIsDropdownOpen] = useState(false);
	const [user, setUser] = useState(null);
	const dropdownRef = useRef(null);
	const navigate = useNavigate();

	useEffect(() => {
		// Get user data on component mount
		const fetchUser = async () => {
			const userData = await getUser();
			setUser(userData);
		};

		fetchUser();
	}, []);

	useEffect(() => {
		// Close dropdown when clicking outside
		const handleClickOutside = (event) => {
			if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
				setIsDropdownOpen(false);
			}
		};

		document.addEventListener("click", handleClickOutside);
		return () => {
			document.removeEventListener("click", handleClickOutside);
		};
	}, []);

	const toggleDropdown = (event) => {
		event.stopPropagation();
		setIsDropdownOpen(!isDropdownOpen);
	};

	const handleLogout = async () => {
		await logout();
		navigate(loginPage);
	};

	return {
		isDropdownOpen,
		setIsDropdownOpen,
		user,
		dropdownRef,
		navigate,
		toggleDropdown,
		handleLogout,
	};
};
