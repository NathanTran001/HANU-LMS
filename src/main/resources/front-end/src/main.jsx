import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import App from "./App.jsx"; // App will now handle routing

createRoot(document.getElementById("root")).render(
	<StrictMode>
		<App />
	</StrictMode>
);
