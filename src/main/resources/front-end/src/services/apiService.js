// apiService.js - Single API service with all operations
import axios from "axios";

// Base configuration
const API_BASE_URL = "http://localhost:8081";

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
		if (error.response?.status === 401) {
			localStorage.removeItem("user");
			window.location.href = "/";
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
	getLecturers: (params) => api.get("/api/lecturers", { params }),
	getLecturer: (id) => api.get(`/api/lecturers/${id}`),
	createLecturer: (data) => api.post("/api/lecturers", data),
	updateLecturer: (id, data) => api.put(`/api/lecturers/${id}`, data),
	deleteLecturer: (id) => api.delete(`/api/lecturers/${id}`),

	// Course operations
	getCourses: (params) => api.get("/api/courses", { params }),
	getCourse: (id) => api.get(`/api/courses/${id}`),
	createCourse: (data) => api.post("/api/courses", data),
	updateCourse: (id, data) => api.put(`/api/courses/${id}`, data),
	deleteCourse: (id) => api.delete(`/api/courses/${id}`),
	getCoursesByFaculty: (facultyId) =>
		api.get(`/api/courses/faculty/${facultyId}`),

	// Faculty operations
	getFaculties: async (page = 0, size = 10) => {
		try {
			const response = await api.get("/api/admin/faculties", {
				params: {
					page,
					size,
					sort: "code,asc", // Optional: add default sorting
				},
				withCredentials: true, // Include cookies in requests
			});
			return {
				content: response.content || [],
				totalPages: response.totalPages || 0,
				totalElements: response.totalElements || 0,
				number: response.number || 0,
				size: response.size || size,
			};
		} catch (error) {
			console.error("Error fetching faculties:", error);
			throw error;
		}
	},
	getFaculty: (code) => api.get(`/api/admin/faculties/${code}`),
	createFaculty: (data) => api.post("/api/admin/faculties", data),
	updateFaculty: (code, data) => api.put(`/api/admin/faculties/${code}`, data),
	deleteFaculty: (code) => api.delete(`/api/admin/faculties/${code}`),

	searchFaculties: async (searchPhrase, page = 0, size = 10) => {
		try {
			const response = await api.get("/api/admin/faculties/search", {
				params: {
					searchPhrase,
					page,
					size,
				},
			});
			return {
				content: response.content || [],
				totalPages: response.totalPages || 0,
				totalElements: response.totalElements || 0,
				number: response.number || 0,
				size: response.size || size,
			};
		} catch (error) {
			console.error("Error searching faculties:", error);
			throw error;
		}
	},

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
