import {
	createBrowserRouter,
	Navigate,
	Route,
	RouterProvider,
	Routes,
	useNavigate,
} from "react-router-dom";
import LoginPage from "./pages/LoginPage.jsx";
import FacultyListPage from "./pages/FacultyListPage.jsx";
import CreateFacultyPage from "./pages/CreateFacultyPage.jsx";
import MyCoursesPage from "./pages/MyCoursesPage.jsx";
import Layout from "./components/Layout.jsx";
import { use, useEffect } from "react";
import { getUser } from "./utils/auth.js";
import {
	SEARCH_COURSE_PAGE,
	CREATE_COURSE_PAGE,
	CREATE_FACULTY_PAGE,
	CREATE_LECTURER_PAGE,
	CREATE_STUDENT_PAGE,
	EDIT_FACULTY_PAGE,
	EDIT_LECTURER_PAGE,
	EDIT_STUDENT_PAGE,
	FACULTY_LIST_PAGE,
	LECTURER_LIST_PAGE,
	LOGIN_PAGE,
	MY_COURSES_PAGE,
	STUDENT_LIST_PAGE,
} from "./constants/paths.js";
import ProtectedRoute from "./components/ProtectedRoute.jsx";
import { AuthProvider } from "./contexts/AuthContext.jsx";
import EditFacultyPage from "./pages/EditFacultyPage.jsx";
import LecturerListPage from "./pages/LecturerListPage.jsx";
import CreateLecturerPage from "./pages/CreateLecturerPage.jsx";
import EditLecturerPage from "./pages/EditLecturerPage.jsx";
import StudentListPage from "./pages/StudentListPage.jsx";
import CreateStudentPage from "./pages/CreateStudentPage.jsx";
import EditStudentPage from "./pages/EditStudentPage.jsx";
import RoleBasedRedirect from "./components/RoleBasedRedirect.jsx";
import CreateCoursePage from "./pages/CreateCoursePage.jsx";
import SearchCoursePage from "./pages/SearchCoursePage.jsx";

const router = createBrowserRouter([
	{
		path: LOGIN_PAGE,
		element: <LoginPage />,
	},
	{
		path: "/",
		element: <RoleBasedRedirect />,
	},
	{
		element: (
			<ProtectedRoute>
				<Layout />
			</ProtectedRoute>
		),
		children: [
			// ADMIN PAGES
			{
				path: FACULTY_LIST_PAGE,
				element: <FacultyListPage />,
			},
			{
				path: CREATE_FACULTY_PAGE,
				element: <CreateFacultyPage />,
			},
			{
				path: EDIT_FACULTY_PAGE,
				element: <EditFacultyPage />,
			},
			{
				path: LECTURER_LIST_PAGE,
				element: <LecturerListPage />,
			},
			{
				path: CREATE_LECTURER_PAGE,
				element: <CreateLecturerPage />,
			},
			{
				path: EDIT_LECTURER_PAGE,
				element: <EditLecturerPage />,
			},
			{
				path: STUDENT_LIST_PAGE,
				element: <StudentListPage />,
			},
			{
				path: CREATE_STUDENT_PAGE,
				element: <CreateStudentPage />,
			},
			{
				path: EDIT_STUDENT_PAGE,
				element: <EditStudentPage />,
			},
			// SHARED PAGES
			{
				path: MY_COURSES_PAGE,
				element: <MyCoursesPage />,
			},
			{
				path: SEARCH_COURSE_PAGE,
				element: <SearchCoursePage />,
			},
			// LECTURER PAGES
			{
				path: CREATE_COURSE_PAGE,
				element: <CreateCoursePage />,
			},
			// STUDENT PAGES
		],
	},
	// {
	// 	path: "/",
	// 	element: (
	// 		<Navigate
	// 			to={LOGIN_PAGE}
	// 			replace
	// 		/>
	// 	),
	// },
	{
		path: "*",
		element: <div>404 Not Found</div>,
	},
]);

export default function App() {
	return (
		<AuthProvider>
			<RouterProvider router={router} />
		</AuthProvider>
	);
}
