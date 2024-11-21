import React from "react";
import axios from "axios";
axios.defaults.withCredentials = true;

export default function NavBar() {
	const handleLogout = async () => {
		try {
			await axios.get("http://localhost:8082/action/logout", null, {
				withCredentials: true,
			});

			console.log("Logout successful:");
			window.location.href = "/"; // Redirect to login page
		} catch (error) {
			console.error("Error during logout:", error);
		}
	};

	return (
		<div>
			<nav
				className="navbar navbar-expand-lg bg-dark border-bottom border-body"
				data-bs-theme="dark"
			>
				<div className="container-fluid">
					<a
						className="navbar-brand fs-3"
						href="/users"
						style={{ margin: "5px" }}
					>
						User Management
					</a>
					<button onClick={handleLogout} className="btn btn-outline-danger">
						Log out
					</button>
				</div>
			</nav>
		</div>
	);
}
