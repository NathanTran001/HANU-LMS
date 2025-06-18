// apiService.js - Single API service with all operations
import axios from "axios";

// Base configuration
const API_BASE_URL = "http://localhost:8080";

// Create axios instance with default config
const apiClient = axios.create({
	baseURL: API_BASE_URL,
	timeout: 10000,
	withCredentials: true, // Include cookies in requests
	headers: {
		"Content-Type": "application/json",
	},
});

// Response interceptor for error handling
apiClient.interceptors.response.use(
	(response) => response,
	(error) => {
		if (error.response?.status === 401 || error.response?.status === 403) {
			window.dispatchEvent(new Event("force-logout"));
		}
		return Promise.reject(error);
	}
);

// Single API service with all operations
const api = {
	// Generic HTTP methods
	get: async (endpoint, config = {}) => {
		try {
			const response = await apiClient.get(endpoint, config);
			return response.data;
		} catch (error) {
			console.error(`GET ${endpoint} failed:`, error);
			throw error;
		}
	},

	post: async (endpoint, data = {}, config = {}) => {
		try {
			const response = await apiClient.post(endpoint, data, config);
			return response.data;
		} catch (error) {
			console.error(`POST ${endpoint} failed:`, error);
			throw error;
		}
	},

	put: async (endpoint, data = {}, config = {}) => {
		try {
			const response = await apiClient.put(endpoint, data, config);
			return response.data;
		} catch (error) {
			console.error(`PUT ${endpoint} failed:`, error);
			throw error;
		}
	},

	delete: async (endpoint, config = {}) => {
		try {
			const response = await apiClient.delete(endpoint, config);
			return response.data;
		} catch (error) {
			console.error(`DELETE ${endpoint} failed:`, error);
			throw error;
		}
	},

	// Auth operations
	login: (credentials) => api.post("/api/auth/login", credentials),
	logout: () => api.post("/api/auth/logout"),
	register: (userData) => api.post("/api/auth/register", userData),

	// User operations
	getProfile: () => api.get("/api/user/profile"),
	updateProfile: (data) => api.put("/api/user/profile", data),

	// Lecturer operations
	getLecturers: (page = 0, size = 10) =>
		api.get("/api/admin/lecturers", { params: { page, size } }),
	getAllLecturers: (code) => {
		const params = code ? { code } : {};
		return api.get("/api/lecturers/all", { params });
	},
	getLecturer: (id) => api.get(`/api/admin/lecturers/${id}`),
	createLecturer: (data) => api.post("/api/admin/lecturers", data),
	updateLecturer: (id, lecturer) =>
		api.put(`/api/admin/lecturers/${id}`, lecturer),
	deleteLecturer: (id) => api.delete(`/api/admin/lecturers/${id}`),
	searchLecturers: (searchPhrase, page = 0, size = 10) =>
		api.get("/api/admin/lecturers/search", {
			params: { searchPhrase, page, size },
		}),

	// Student operations
	getStudents: (page = 0, size = 10) =>
		api.get("/api/admin/students", { params: { page, size } }),
	getStudent: (id) => api.get(`/api/admin/students/${id}`),
	createStudent: (student) => api.post("/api/admin/students", student),
	updateStudent: (id, student) => api.put(`/api/admin/students/${id}`, student),
	deleteStudent: (id) => api.delete(`/api/admin/students/${id}`),
	searchStudents: (searchPhrase, page = 0, size = 10) =>
		api.get("/api/admin/students/search", {
			params: { searchPhrase, page, size },
		}),

	// Faculty operations
	getFaculties: (page = 0, size = 10) =>
		api.get("/api/admin/faculties", {
			params: { page, size },
		}),
	getAllFaculties: () => api.get("/api/faculties/all"),
	getFaculty: (code) => api.get(`/api/admin/faculties/${code}`),
	createFaculty: (faculty) => api.post("/api/admin/faculties", faculty),
	updateFaculty: (code, faculty) =>
		api.put(`/api/admin/faculties/${code}`, faculty),
	deleteFaculty: (code) => api.delete(`/api/admin/faculties/${code}`),
	searchFaculties: (searchPhrase, page = 0, size = 10) =>
		api.get("/api/admin/faculties/search", {
			params: {
				searchPhrase,
				page,
				size,
			},
		}),

	// Course operations
	getCourses: (page = 0, size = 10) =>
		api.get("/api/courses", { params: { page, size } }),
	getCourse: (code) => api.get(`/api/courses/${code}`),
	createCourse: (data) => api.post("/api/courses", data),
	updateCourse: (code, data) => api.put(`/api/courses/${code}`, data),
	deleteCourse: (code) => api.delete(`/api/courses/${code}`),
	getCoursesByFaculty: (facultyId) =>
		api.get(`/api/courses/faculty/${facultyId}`),
	searchCourse: (searchPhrase, page = 0, size = 10) =>
		api.get("/api/courses/search", {
			params: { searchPhrase, page, size },
		}),

	// File upload
	uploadFile: async (endpoint, file, additionalData = {}) => {
		const formData = new FormData();
		formData.append("file", file);

		Object.keys(additionalData).forEach((key) => {
			formData.append(key, additionalData[key]);
		});

		try {
			const response = await apiClient.post(endpoint, formData, {
				headers: {
					"Content-Type": "multipart/form-data",
				},
			});
			return response.data;
		} catch (error) {
			console.error(`File upload to ${endpoint} failed:`, error);
			throw error;
		}
	},
};

export { API_BASE_URL };
export default api;
