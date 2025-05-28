// components/ProtectedRoute.js
import { LOGIN_PAGE } from "../constants/paths";
import { useAuth } from "../contexts/AuthContext.jsx";
import { Navigate } from "react-router-dom";

function ProtectedRoute({ children }) {
	const { isAuthenticated, loading } = useAuth();

	if (loading) {
		return <div>Loading...</div>; // Or your loading spinner
	}

	if (!isAuthenticated) {
		return (
			<Navigate
				to={LOGIN_PAGE}
				replace
			/>
		);
	}

	return children;
}

export default ProtectedRoute;
