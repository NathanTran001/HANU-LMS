// utils/auth.js
import axios from "axios";
import { API_BASE_URL } from "../services/apiService";

// Set up axios defaults
axios.defaults.withCredentials = true;

export const getUser = async () => {
	try {
		const response = await axios.get(API_BASE_URL + "/api/auth/me");
		console.log("User fetched:", response.data);

		return response.data;
	} catch (error) {
		console.error("Error fetching user:", error);
		return null;
	}
};

export const getUserRole = async () => {
	try {
		const user = await getUser();
		console.log(user?.role);

		return user?.role;
	} catch (error) {
		console.error("Error fetching user role:", error);
		return "";
	}
};

export const logout = async () => {
	try {
		await axios.post(API_BASE_URL + "/api/auth/logout");
		// Clear any local storage if needed
		// localStorage.removeItem("user");
	} catch (error) {
		console.error("Error during logout:", error);
	}
};
