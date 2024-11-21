import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { useMessage } from "../MessageContext";
axios.defaults.withCredentials = true;

export default function Login() {
	const [email, setEmail] = useState("");
	const [password, setPassword] = useState("");
	const [loading, setLoading] = useState(false);
	const { message, setMessage } = useMessage();
	const navigate = useNavigate();

	const handleLogin = async (e) => {
		e.preventDefault();
		setLoading(true);

		try {
			const loginRequest = { email, password };
			const response = await axios.post(
				"http://localhost:8082/action/login",
				loginRequest,
				{ withCredentials: true } // Important: Allow cookies to be sent and received
			);

			if (response.status === 200) {
				setMessage("Login successful!");
				navigate("/users");
			} else {
				setMessage("Invalid credentials or user is disabled.");
			}
		} catch (error) {
			console.error("Error during login:", error);
			setMessage("An error occurred during login.");
		} finally {
			setLoading(false);
		}
	};

	const handleGoogleLogin = async () => {
		setLoading(true);
		try {
			window.location.href =
				"http://localhost:8082/oauth2/authorization/google";
		} catch (error) {
			setMessage("Login failed. Please try again.");
			setLoading(false);
		}
	};

	return (
		<div className="container" style={{ maxWidth: "400px", marginTop: "50px" }}>
			<div
				className="card p-4"
				style={{
					border: "1px solid #ddd",
					borderRadius: "8px",
					boxShadow: "0 4px 8px rgba(0, 0, 0, 0.75)", // Added shadow effect here
				}}
			>
				<h2 className="text-center">Login</h2>
				{message && (
					<div className="alert alert-info text-center" role="alert">
						{message}
					</div>
				)}
				<form onSubmit={handleLogin}>
					<div className="mb-3">
						<label htmlFor="email" className="form-label">
							Email
						</label>
						<input
							type="email"
							className="form-control"
							id="email"
							value={email}
							onChange={(e) => setEmail(e.target.value)}
							required
						/>
					</div>
					<div className="mb-3">
						<label htmlFor="password" className="form-label">
							Password
						</label>
						<input
							type="password"
							className="form-control"
							id="password"
							value={password}
							onChange={(e) => setPassword(e.target.value)}
							required
						/>
					</div>
					<div className="text-center mt-4">
						<button
							type="submit"
							className="btn btn-primary"
							style={{
								width: "100%",
								padding: "10px",
								fontSize: "16px",
							}}
							disabled={loading}
						>
							{loading ? "Logging in..." : "Login"}
						</button>
					</div>
					<div className="text-center mt-2">
						<button
							onClick={handleGoogleLogin}
							className="btn btn-danger"
							style={{
								width: "100%",
								padding: "10px",
								fontSize: "16px",
							}}
						>
							<i className="fa fa-brands fa-google"></i> Login with Google
						</button>
					</div>
				</form>
			</div>
		</div>
	);
}
