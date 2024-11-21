package com.tech.test.service;

import com.tech.test.entity.User;
import com.tech.test.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    private static final String PHOTO_UPLOAD_DIR = "photos/";

    @Autowired
    private UserRepository userRepository;

    // Create a new user
    public User createUser(User user) {
        User parent = user.getParent();
        if (parent!=null) {
            String allParentIds = parent.getAllParentIDs() == null ? "-" : parent.getAllParentIDs();
            allParentIds += String.valueOf(parent.getId()) + "-";

            user.setParent(parent);
            user.setAllParentIDs(allParentIds);
        }

        return userRepository.save(user);
    }

    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get a user by ID
    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    // Update a user
    public User updateUser(Integer id, User user) {
        if (userRepository.existsById(id)) {
            user.setId(id);
            return userRepository.save(user);
        } else {
            return null;
        }
    }

    // Delete a user
    @Transactional
    public boolean deleteUser(Integer id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteUserById(id);
            return true;
        } else {
            return false;
        }
    }

    public User authenticateUser(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            return user.get();
        }
        return null;
    }

    public Page<User> getUsers(String search, Pageable pageable) {
        if (search == null || search.isEmpty()) {
            return userRepository.findAll(pageable);  // No search, just return paginated users
        } else {
            return userRepository.findByNameContainingIgnoreCase(search, pageable);  // Search by name, case-insensitive
        }
    }

    public String savePhoto(MultipartFile photo) {
        try {
            // Generate a unique file name based on timestamp or UUID
            String filename = System.currentTimeMillis() + "_" + photo.getOriginalFilename();
            Path path = Paths.get(PHOTO_UPLOAD_DIR + filename);
            Files.createDirectories(path.getParent());
            Files.write(path, photo.getBytes()); // Save the file

            return path.toString(); // Return the file path or URL
        } catch (IOException e) {
            throw new RuntimeException("Failed to save the photo", e);
        }
    }

    public void updateUserEnabledStatus(Integer id, boolean enabled) {
        userRepository.updateEnabledStatus(id, enabled);
    }
}
