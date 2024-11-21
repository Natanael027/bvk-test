## Project Overview

This app is designed to manage organization users by allowing administrators to add, view, and organize users in an interactive and easy-to-navigate interface. 
The backend provides the necessary endpoints for the frontend to perform CRUD operations on organization users, and security features to login using google account.

---

## Frontend

### Login/Register Page

- **User-friendly login and registration page**: A page where users can log in or register with the application.
- **Google Sign-In Integration**: Users can authenticate using their Google account.

### User Page

- **User List Page**: A page that displays all organization users with their name and position.
- **Search and Pagination**: Users can search for specific users and paginate through the list of users.
- **Add New User Page**: A page to add new users to the organization.
- **User Detail Page**: A detailed view for each user showing their picture, name, and position.

---

## Backend

### API Endpoints

- **GET `/user/all`**: Fetch all organization users.
- **GET `/user/home`**: Fetch all users with pagination.
- **POST `/user/create`**: Add a new user to the organization.
- **GET `/user/{id}`**: Fetch a user's details by ID.
- **GET `/user/{id}/enabled/{status}`**: Enable/Disable user's status by ID.
- **PUT `/user/update/{id}`**: Update a user's details.
- **DELETE `/user/delete/{id}`**: Remove a user from the organization.

### Security
- **Spring Cloud Security**: Optional integration to manage authentication and authorization.

---

## Technologies

- **Frontend**:
    - React.js (for building the user interface)
    - Google Sign-In (for authentication)

- **Backend**:
    - Spring Boot (for backend development)
    - MySQL (for data storage)
    - Docker (for images & container)

