export default function getErrorMessages(error) {
	if (error.response) {
		const data = error.response.data;

		// If data is already an array (your validation errors)
		if (Array.isArray(data)) {
			return data.filter((msg) => msg); // Filter out empty strings
		}

		// If data has an error property that's an array
		if (Array.isArray(data.error)) {
			return data.error.filter((msg) => msg);
		}

		// If data has errors property (common validation response format)
		if (Array.isArray(data.errors)) {
			return data.errors.filter((msg) => msg);
		}

		// Fallback
		return ["Unknown error occurred."];
	} else {
		// Network/connection errors - ALWAYS return array
		return ["Network error. Please try again."];
	}
}
