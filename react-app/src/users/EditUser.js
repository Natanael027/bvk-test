import React, { useState, useEffect } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import axios from "axios";
import { useMessage } from "../MessageContext";

export default function EditUser() {
	const { userId } = useParams();
	const navigate = useNavigate();
	const { setMessage } = useMessage();

	const [user, setUser] = useState({
		name: "",
		email: "",
		password: "",
		position: "",
		enabled: true,
		photos: null,
		parent: null,
	});

	const [users, setUsers] = useState([]);

	const { name, email, password, position, enabled, photos, parent } = user;

	// Fetch user data when the component mounts or userId changes
	useEffect(() => {
		const fetchUserData = async () => {
			try {
				const result = await axios.get(`http://localhost:8082/user/${userId}`, {
					withCredentials: true,
				});
				setUser(result.data);
			} catch (error) {
				console.error("Error fetching user data:", error);
			}
		};

		const fetchUsers = async () => {
			try {
				const result = await axios.get("http://localhost:8082/user/all", {
					withCredentials: true,
				});
				setUsers(result.data);
			} catch (error) {
				console.error("Error fetching users:", error);
			}
		};

		fetchUserData();
		fetchUsers(); // Fetch the list of all users for the parent dropdown
	}, [userId]);

	const onInputChange = (e) => {
		setUser({ ...user, [e.target.name]: e.target.value });
	};

	const onFileChange = (e) => {
		const file = e.target.files[0];
		if (file) {
			setUser({ ...user, photos: file });
		} else {
			setUser({ ...user, photos: null });
		}
	};

	const onParentChange = (e) => {
		const selectedParentId = e.target.value;
		setUser({ ...user, parent: selectedParentId });
	};

	const onSubmit = async (e) => {
		e.preventDefault();

		const formData = new FormData();
		formData.append("name", name);
		formData.append("email", email);
		formData.append("password", password);
		formData.append("position", position);
		formData.append("enabled", enabled);
		formData.append("parent", parent);

		if (photos) {
			formData.append("photos", photos);
		}

		try {
			// Make the PUT request to the API endpoint
			const response = await axios.put(
				`http://localhost:8082/user/update/${userId}`,
				formData,
				{
					headers: {
						"Content-Type": "multipart/form-data",
					},
				},
				{ withCredentials: true }
			);

			if (response.status === 200) {
				setMessage("User updated successfully.");
				navigate("/users");
			} else {
				console.error("There was an error updating the user:", response);
				setMessage("Error updating user.");
			}
		} catch (error) {
			console.error("There was an error updating the user:", error);
			setMessage("Error updating user.");
		}
	};

	return (
		<div className="container">
			<div className="row">
				<div className="col-md-6 offset-md-3 border rounded p-4 mt-2 shadow">
					<h2 className="text-center m-4">Edit User</h2>

					<form onSubmit={onSubmit}>
						<div className="mb-3">
							<label htmlFor="email" className="form-label">
								E-mail
							</label>
							<input
								type="text"
								className="form-control"
								placeholder="Enter e-mail address"
								name="email"
								value={email}
								required
								onChange={onInputChange}
							/>
						</div>
						<div className="mb-3">
							<label htmlFor="name" className="form-label">
								Name
							</label>
							<input
								type="text"
								className="form-control"
								placeholder="Enter name"
								name="name"
								value={name}
								required
								onChange={onInputChange}
							/>
						</div>
						<div className="mb-3">
							<label htmlFor="password" className="form-label">
								Password
							</label>
							<input
								type="text"
								className="form-control"
								placeholder="Enter password"
								name="password"
								value={password}
								required
								onChange={onInputChange}
							/>
						</div>
						<div className="mb-3">
							<label htmlFor="position" className="form-label">
								Position
							</label>
							<input
								type="text"
								className="form-control"
								placeholder="Enter position"
								name="position"
								value={position}
								required
								onChange={onInputChange}
							/>
						</div>

						<div className="mb-3">
							<label htmlFor="photo" className="form-label">
								Upload Photo
							</label>
							<input
								type="file"
								className="form-control"
								accept="image/*"
								name="photos"
								onChange={onFileChange}
							/>

							{photos && photos instanceof File ? (
								<div className="mt-3">
									<img
										src={URL.createObjectURL(photos)} // Preview photo
										alt="Uploaded preview"
										className="img-fluid"
										style={{ maxHeight: "200px" }}
									/>
								</div>
							) : (
								// If no new photo is uploaded, show the existing one if available
								<div className="mt-3">
									{user.photos ? (
										<img
											src={`data:image/jpeg;base64,${user.photos}`}
											alt={user.name}
											style={{
												width: "100px",
												height: "70px",
												objectFit: "cover",
											}}
										/>
									) : (
										<span>No photo</span>
									)}
								</div>
							)}
						</div>

						<div className="mb-3">
							<label htmlFor="parent" className="form-label">
								Select Parent
							</label>
							<select
								name="parent"
								className="form-control"
								value={parent || ""}
								onChange={onParentChange}
							>
								<option value="">Select a parent</option>
								{users.map((u) => (
									<option key={u.id} value={u.id}>
										{u.name} ({u.position})
									</option>
								))}
							</select>
						</div>

						<div className="d-flex justify-content-end mt-3">
							<button type="submit" className="btn btn-outline-primary">
								Submit
							</button>
							<Link className="btn btn-outline-danger mx-2" to="/users">
								Cancel
							</Link>
						</div>
					</form>
				</div>
			</div>
		</div>
	);
}
