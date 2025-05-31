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
	CREATE_FACULTY_PAGE,
	EDIT_FACULTY_PAGE,
	FACULTY_LIST_PAGE,
	LOGIN_PAGE,
	MY_COURSES_PAGE,
} from "./constants/paths.js";
import ProtectedRoute from "./components/ProtectedRoute.jsx";
import { AuthProvider } from "./contexts/AuthContext.jsx";
import EditFacultyPage from "./pages/EditFacultyPage.jsx";

const router = createBrowserRouter([
	{
		path: LOGIN_PAGE,
		element: <LoginPage />,
	},
	{
		path: FACULTY_LIST_PAGE,
		element: (
			<ProtectedRoute>
				<Layout>
					<FacultyListPage />
				</Layout>
			</ProtectedRoute>
		),
	},
	{
		path: CREATE_FACULTY_PAGE,
		element: (
			<ProtectedRoute>
				<Layout>
					<CreateFacultyPage />
				</Layout>
			</ProtectedRoute>
		),
	},
	{
		path: EDIT_FACULTY_PAGE,
		element: (
			<ProtectedRoute>
				<Layout>
					<EditFacultyPage />
				</Layout>
			</ProtectedRoute>
		),
	},
	{
		path: MY_COURSES_PAGE,
		element: (
			<ProtectedRoute>
				<Layout>
					<MyCoursesPage />
				</Layout>
			</ProtectedRoute>
		),
	},
	{
		path: "/",
		element: (
			<Navigate
				to={LOGIN_PAGE}
				replace
			/>
		),
	},
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
