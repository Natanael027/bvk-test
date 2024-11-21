import React from "react";
import "./App.css";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./auth/Login";
import Register from "./auth/Register";
import "../node_modules/bootstrap/dist/css/bootstrap.min.css";
import "../node_modules/font-awesome/css/font-awesome.min.css";
import { MessageProvider } from "./MessageContext";
import Navbar from "./layout/Navbar";
import Home from "./pages/Home";
import AddUser from "./users/AddUser";
import EditUser from "./users/EditUser";
import axios from "axios";

// Set default axios configurations globally
axios.defaults.withCredentials = true;

const App = () => {
	return (
		<div className="App">
			<MessageProvider>
				<Router>
					<Navbar />
					<Routes>
						<Route exact path="/users" element={<Home />} />
						<Route exact path="/users/add" element={<AddUser />} />
						<Route exact path="/users/edit/:userId" element={<EditUser />} />

						<Route path="/" element={<Login />} />
						<Route path="/register" element={<Register />} />
					</Routes>
				</Router>
			</MessageProvider>
		</div>
	);
};

export default App;
