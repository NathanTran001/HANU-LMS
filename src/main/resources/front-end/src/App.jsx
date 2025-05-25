import { createBrowserRouter, RouterProvider } from "react-router-dom";
import LoginPage from "./pages/LoginPage.jsx";
import FacultyListPage from "./pages/FacultyListPage.jsx";
import CreateFacultyPage from "./pages/CreateFacultyPage.jsx";
import MyCoursesPage from "./pages/MyCoursesPage.jsx";
import Layout from "./components/Layout.jsx";

export const loginPage = "/";
export const facultyListPage = "/admin/listFaculty";
export const createFacultyPage = "/admin/createFaculty";
export const myCoursesPage = "/myCourses";

const router = createBrowserRouter([
	{
		path: loginPage,
		element: <LoginPage />,
		errorElement: <div>404 Not Found Ight?</div>,
	},
	{
		path: facultyListPage,
		element: (
			<Layout>
				<FacultyListPage />
			</Layout>
		),
	},
	{
		path: createFacultyPage,
		element: <CreateFacultyPage />,
	},
	{
		path: myCoursesPage,
		element: (
			<Layout>
				<MyCoursesPage />
			</Layout>
		),
	},
]);

export default function App() {
	return <RouterProvider router={router} />;
}
