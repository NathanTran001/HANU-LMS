import { createContext, useContext, useEffect, useState } from "react";
import { getUser } from "../utils/auth";

const AuthContext = createContext();

export function useAuth() {
	const context = useContext(AuthContext);
	if (!context) {
		throw new Error("useAuth must be used within AuthProvider");
	}
	return context;
}

export function AuthProvider({ children }) {
	const [isAuthenticated, setIsAuthenticated] = useState(false);
	const [user, setUser] = useState(null);
	const [loading, setLoading] = useState(true);

	useEffect(() => {
		// Check auth status on app load
		checkAuthStatus();
	}, []);

	const checkAuthStatus = async () => {
		try {
			const userData = await getUser();
			setUser(userData);
			setIsAuthenticated(true);
		} catch (error) {
			setIsAuthenticated(false);
		} finally {
			setLoading(false);
		}
	};

	return (
		<AuthContext.Provider
			value={{ isAuthenticated, user, checkAuthStatus, loading }}
		>
			{children}
		</AuthContext.Provider>
	);
}
