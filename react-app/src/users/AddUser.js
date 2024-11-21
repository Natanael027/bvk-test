import axios from "axios";
import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useMessage } from "../MessageContext";

export default function AddUser() {
	const { setMessage } = useMessage();
	const navigate = useNavigate();

	const [user, setUser] = useState({
		name: "",
		email: "",
		password: "",
		position: "",
		enabled: true,
		hasChildren: false,
		photos: null,
		parent: null,
	});

	const { name, email, password, position, enabled, photos, parent } =
		user;
	const [users, setUsers] = useState([]);

	useEffect(() => {
		const fetchUsers = async () => {
			const result = await axios.get("http://localhost:8082/user/all", {
				withCredentials: true,
			});
			setUsers(result.data);
		};
		fetchUsers();
	}, []);

	const onInputChange = (e) => {
		setUser({ ...user, [e.target.name]: e.target.value });
	};

	const onFileChange = (e) => {
		const file = e.target.files[0];
		if (file) {
			setUser({ ...user, photos: file });
		}
	};

	const onParentChange = (e) => {
		const selectedParentId = e.target.value;
		setUser({ ...user, parent: selectedParentId });
	};

	const onSubmit = async (e) => {
		e.preventDefault();

		// Prepare the form data
		const formData = new FormData();
		formData.append("name", name);
		formData.append("email", email);
		formData.append("password", password);
		formData.append("position", position);
		formData.append("enabled", enabled);
		formData.append("parent", parent);

		// Append the photo if it exists
		if (photos) {
			formData.append("photos", photos);
		}

		try {
			await axios.post(
				"http://localhost:8082/user/create",
				formData,
				{
					headers: {
						"Content-Type": "multipart/form-data",
					},
				},
				{ withCredentials: true }
			);

			setMessage("User created successfully.");
			navigate("/users");
		} catch (error) {
			console.error("There was an error uploading the user:", error);
			setMessage("Error creating user.");
		}
	};

	return (
		<div className="container">
			<div className="row">
				<div className="col-md-6 offset-md-3 border rounded p-4 mt-2 shadow">
					<h2 className="text-center m-4">Register User</h2>

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
								required
								onChange={onFileChange}
							/>
							{photos && (
								<div className="mt-3">
									<img
										src={URL.createObjectURL(photos)} // preview
										alt="Uploaded preview"
										className="img-fluid"
										style={{ maxHeight: "200px" }}
									/>
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
