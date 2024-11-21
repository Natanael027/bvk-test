import React, { createContext, useState, useContext } from "react";

const MessageContext = createContext();
export const useMessage = () => {
	return useContext(MessageContext);
};

export const MessageProvider = ({ children }) => {
	const [message, setMessage] = useState(""); // State to hold the message

	const handleMessage = (msg) => {
		setMessage(msg); 
		setTimeout(() => {
			setMessage(""); // Clear the message after 2 seconds
		}, 2000); 
	};

	return (
		<MessageContext.Provider value={{ message, setMessage: handleMessage }}>
			{children} {/* Render children components */}
		</MessageContext.Provider>
	);
};
