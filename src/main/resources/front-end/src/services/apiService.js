// apiService.js - Single API service with all operations
import axios from "axios";

// Base configuration
const API_BASE_URL = "http://localhost:8080";

// Create axios instance with default config
const apiClient = axios.create({
	baseURL: API_BASE_URL,
	timeout: 10000,
	withCredentials: true, // Include cookies in requests
	// headers: {
	// 	"Content-Type": "application/json",
	// },
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

	// COURSE
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

	// TOPIC
	createTopic: (courseCode, topic) =>
		api.post(`/api/topic/${courseCode}`, topic),
	updateTopic: (id, topic) => api.put(`/api/topic/${id}`, topic),
	deleteTopic: (id) => api.delete(`/api/topic/${id}`),

	// TOPIC ITEM
	createUrlTopicItem: (title, url, topicId) =>
		api.post("/api/topic-item/url", null, { params: { title, url, topicId } }),

	createFileTopicItem: (title, file, topicId) => {
		const formData = new FormData();
		formData.append("title", title);
		formData.append("file", file);
		formData.append("topicId", topicId);
		return api.post("/api/topic-item/file", formData);
	},

	createFolderTopicItem: (title, file, topicId) => {
		const formData = new FormData();
		formData.append("title", title);
		formData.append("file", file);
		formData.append("topicId", topicId);
		return api.post("/api/topic-item/folder", formData);
	},

	updateTopicItem: (id, title, url) =>
		api.put(`/api/topic-item/${id}`, null, { params: { title, url } }),

	replaceTopicItemFile: (id, file) => {
		const formData = new FormData();
		formData.append("file", file);
		return api.put(`/api/topic-item/${id}/file`, formData);
	},
	getTopicItemDownloadUrl: (id) => api.get(`/api/topic-item/${id}/download`),

	deleteTopicItem: (id) => api.delete(`/api/topic-item/${id}`),
};

export { API_BASE_URL };
export default api;
