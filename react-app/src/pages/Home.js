import React, { useState, useEffect } from "react";
import axios from "axios";
import { Link } from "react-router-dom";
import { useMessage } from "../MessageContext";
axios.defaults.withCredentials = true;

export default function Home() {
	const { message, setMessage } = useMessage();
	const [users, setUsers] = useState([]);
	const [userHierarchy, setUserHierarchy] = useState({});
	const [searchQuery, setSearchQuery] = useState("");
	const [currentPage, setCurrentPage] = useState(1);
	const [totalPages, setTotalPages] = useState(1);
	const pageSize = 10;

	useEffect(() => {
		loadUsers(currentPage, searchQuery);
	}, [currentPage, searchQuery]);

	const loadUsers = async (page, search) => {
		try {
			const result = await axios.get("http://localhost:8082/user/home", {
				params: {
					page: page - 1,
					size: pageSize,
					search: search,
				},
				withCredentials: true,
			});

			const usersData = result.data.content || [];
			setUsers(usersData);
			setTotalPages(result.data.totalPages || 1);
			calculateHierarchyDepth(usersData);
		} catch (error) {
			console.error("Error fetching users:", error);
			setMessage("Error loading users.");
			clearMessageAfterTimeout();
		}
	};

	const calculateHierarchyDepth = (usersData) => {
		const hierarchy = {};

		const findHierarchyLevel = (userId) => {
			let user = usersData.find((user) => user.id === userId);
			let level = 0;

			while (user && user.parent !== null) {
				level++;
				user = usersData.find((u) => u.id === user.parent);
			}

			return level;
		};

		usersData.forEach((user) => {
			hierarchy[user.id] = findHierarchyLevel(user.id);
		});

		setUserHierarchy(hierarchy);
	};

	const getModifiedPosition = (user) => {
		let position = user.position || "";
		let level = userHierarchy[user.id] || 0;
		const modifiedPosition = "-".repeat(level) + position;
		return modifiedPosition;
	};

	const toggleUserStatus = async (userId, currentStatus) => {
		try {
			const newStatus = !currentStatus;
			await axios.get(
				`http://localhost:8082/user/${userId}/enabled/${newStatus}`,
				{ withCredentials: true }
			);
			setUsers((prevUsers) =>
				prevUsers.map((user) =>
					user.id === userId ? { ...user, enabled: newStatus } : user
				)
			);
			setMessage("User status updated successfully.");
			clearMessageAfterTimeout();
		} catch (error) {
			console.error("Error updating user status:", error);
			setMessage("Error updating user status.");
			clearMessageAfterTimeout();
		}
	};

	const deleteUser = async (userId) => {
		if (window.confirm("Are you sure you want to delete this user?")) {
			try {
				await axios.delete(`http://localhost:8082/user/delete/${userId}`, {
					withCredentials: true,
				});
				setUsers((prevUsers) => prevUsers.filter((user) => user.id !== userId));
				setMessage("User deleted successfully.");
				clearMessageAfterTimeout();
			} catch (error) {
				console.error("Error deleting user:", error);
				setMessage("Error deleting user.");
				clearMessageAfterTimeout();
			}
		}
	};

	const clearMessageAfterTimeout = () => {
		setTimeout(() => {
			setMessage("");
		}, 2000);
	};

	const handleSearchChange = (e) => {
		setSearchQuery(e.target.value);
		setCurrentPage(1);
	};

	const handlePageChange = (newPage) => {
		setCurrentPage(newPage);
	};

	const messageStyle = {
		marginTop: "10px",
		fontWeight: "bold",
		textAlign: "center",
		color: "#424242",
	};

	return (
		<div className="container-fluid">
			{message && <div style={messageStyle}>{message}</div>}

			<div className="py-4">
				<h1 className="mb-4" style={{ textAlign: "center" }}>
					Users List
				</h1>

				{/* Search Section and Create New User Button in One Line */}
				<div className="d-flex justify-content-between align-items-center mb-4">
					{/* Search Label and Input */}
					<div className="d-flex align-items-center">
						<label htmlFor="search" className="form-label mb-0 me-2">
							Search
						</label>
						<input
							type="text"
							className="form-control"
							id="search"
							placeholder="Search by Name"
							value={searchQuery}
							onChange={handleSearchChange}
						/>
					</div>

					{/* Create New User Button */}
					<Link className="btn btn-outline-secondary" to="/users/add">
						Create New User
					</Link>
				</div>

				<div className="table-responsive">
					{users.length > 0 ? (
						<table
							className="table table-hover border shadow-lg rounded"
							style={{ tableLayout: "fixed" }}
						>
							<thead className="table-dark">
								<tr>
									<th
										scope="col"
										style={{ width: "50px", textAlign: "center" }}
									>
										No
									</th>
									<th scope="col">Name</th>
									<th scope="col" className="hide-phone">
										Email
									</th>
									<th scope="col">Position</th>
									<th
										scope="col"
										className="hide-photo"
										style={{ width: "150px" }}
									>
										Photos
									</th>
									<th scope="col" className="hide-photo">
										Enabled
									</th>
									<th scope="col">Action</th>
								</tr>
							</thead>
							<tbody>
								{users.map((user, index) => {
									const modifiedPosition = getModifiedPosition(user);
									return (
										<tr key={user.id}>
											<td style={{ width: "50px", textAlign: "center" }}>
												{index + 1 + (currentPage - 1) * pageSize}
											</td>
											<td>{user.name}</td>
											<td className="hide-phone">{user.email}</td>
											<td>{modifiedPosition}</td>
											<td className="hide-photo">
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
											</td>
											<td className="hide-photo">
												<a
													href="#"
													onClick={() =>
														toggleUserStatus(user.id, user.enabled)
													}
													style={{
														fontSize: "30px",
														color: user.enabled ? "green" : "red",
													}}
												>
													{user.enabled ? (
														<i className="fa fa-check-circle"></i>
													) : (
														<i className="fa fa-times-circle"></i>
													)}
												</a>
											</td>
											<td>
												<Link
													to={`/users/edit/${user.id}`}
													className="btn btn-outline-info mx-2 btn-sm"
												>
													Edit
												</Link>
												<button
													type="button"
													className="btn btn-danger mx-2 btn-sm"
													onClick={() => deleteUser(user.id)}
												>
													Delete
												</button>
											</td>
										</tr>
									);
								})}
							</tbody>
						</table>
					) : (
						<div>No users found.</div>
					)}

					<nav aria-label="Page navigation">
						<ul className="pagination justify-content-center">
							<li
								className={`page-item ${currentPage === 1 ? "disabled" : ""}`}
							>
								<button
									className="page-link"
									onClick={() => handlePageChange(currentPage - 1)}
								>
									Previous
								</button>
							</li>
							{[...Array(totalPages)].map((_, index) => (
								<li
									key={index}
									className={`page-item ${
										currentPage === index + 1 ? "active" : ""
									}`}
								>
									<button
										className="page-link"
										onClick={() => handlePageChange(index + 1)}
									>
										{index + 1}
									</button>
								</li>
							))}
							<li
								className={`page-item ${
									currentPage === totalPages ? "disabled" : ""
								}`}
							>
								<button
									className="page-link"
									onClick={() => handlePageChange(currentPage + 1)}
								>
									Next
								</button>
							</li>
						</ul>
					</nav>
				</div>
			</div>
		</div>
	);
}
