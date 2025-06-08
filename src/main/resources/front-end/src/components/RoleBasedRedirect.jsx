import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";
import {
	FACULTY_LIST_PAGE,
	MY_COURSES_PAGE,
	LOGIN_PAGE,
} from "../constants/paths";

const RoleBasedRedirect = () => {
	const { user, isAuthenticated, loading } = useAuth();
	const navigate = useNavigate();

	useEffect(() => {
		if (loading) return;
		if (!isAuthenticated) {
			navigate(LOGIN_PAGE, { replace: true });
		} else if (user?.role?.toLowerCase() === "admin") {
			navigate(FACULTY_LIST_PAGE, { replace: true });
		} else if (
			user?.role?.toLowerCase() === "academicUser" ||
			user?.role?.toLowerCase() === "student"
		) {
			navigate(MY_COURSES_PAGE, { replace: true });
		} else {
			navigate(LOGIN_PAGE, { replace: true });
		}
	}, [user, isAuthenticated, loading, navigate]);

	return <div>Redirecting...</div>;
};

export default RoleBasedRedirect;
