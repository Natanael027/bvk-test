package com.tech.test.controller;

import com.tech.test.entity.User;
import com.tech.test.model.UserDTO;
import com.tech.test.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:3001", allowCredentials = "true")
public class UserController {

    @Autowired
    private UserService userService;

    // Create a new user
    @PostMapping("/create")
    public ResponseEntity<UserDTO> createUser(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("position") String position,
            @RequestParam("enabled") boolean enabled,
            @RequestParam("parent") String parent,
            @RequestParam(value = "photos", required = false) MultipartFile photos
    ) throws IOException {
        String photoUrl = null;
        if (photos != null && !photos.isEmpty()) {
            photoUrl = encodeImageToBase64(photos);

        }

        User userParent = null;
        if (parent!=null && !parent.isEmpty() && !parent.equalsIgnoreCase("null")){
            userParent = userService.getUserById(Integer.valueOf(parent)).get();
        }
        User newUser = new User(name, email, password, position, enabled, userParent, photoUrl);
        User createdUser = userService.createUser(newUser);
        UserDTO userDTO = createdUser.toDTO();
        return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
    }

    @GetMapping("/home")
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Create pageable object to pass pagination parameters
        Pageable pageable = PageRequest.of(page, size);

        // Call service method to get paginated and filtered data
        Page<User> userPage = userService.getUsers(search, pageable);

        // Convert the user entities to DTOs
        List<UserDTO> userDTOs = userPage.getContent().stream()
                .map(user -> user.toDTO())
                .collect(Collectors.toList());

        // Prepare the response
        Map<String, Object> response = new HashMap<>();
        response.put("content", userDTOs);
        response.put("totalPages", userPage.getTotalPages());
        response.put("totalElements", userPage.getTotalElements());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Get all users
    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();

        List<UserDTO> userDTOs = users.stream()
                .map(user -> user.toDTO())
                .collect(Collectors.toList());

        return new ResponseEntity<>(userDTOs, HttpStatus.OK);
    }

    // Get a user by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Integer id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(u -> new ResponseEntity<>(u.toDTO(), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{id}/enabled/{status}")
    public ResponseEntity<String> updateUserEnabledStatus(@PathVariable("id") Integer id,
                                          @PathVariable("status") boolean enabled)
    {
        userService.updateUserEnabledStatus(id, enabled);

        String status = enabled ? "enabled" : "disabled";
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    // Update an existing user
    @PutMapping("/update/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Integer id,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("position") String position,
            @RequestParam("enabled") boolean enabled,
            @RequestParam("parent") String parent,
            @RequestParam(value = "photos", required = false) MultipartFile photos) throws IOException {
        User currentUser = userService.getUserById(id).orElse(null);
        if (currentUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        String photoUrl = currentUser.getPhotos();
        if (photos != null && !photos.isEmpty()) {
            photoUrl = encodeImageToBase64(photos);
        }

        User userParent = null;
        if (parent != null && !parent.isEmpty() && !parent.equalsIgnoreCase("null")) {
            userParent = userService.getUserById(Integer.valueOf(parent)).orElse(null);
        }

        User updatedUser = new User(name, email, password, position, enabled, userParent, photoUrl);
        updatedUser.setId(id);
        User result = userService.updateUser(id, updatedUser);

        return result != null ? new ResponseEntity<>(result.toDTO(), HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Delete a user
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        boolean isDeleted = userService.deleteUser(id);
        return isDeleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Helper method to encode the image to Base64
    private String encodeImageToBase64(MultipartFile photo) throws IOException {
        byte[] bytes = photo.getBytes();
        return Base64.getEncoder().encodeToString(bytes);
    }
}

