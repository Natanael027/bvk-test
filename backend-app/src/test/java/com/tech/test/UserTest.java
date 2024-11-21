package com.tech.test;

import com.tech.test.entity.User;
import com.tech.test.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserTest {

    @Autowired
    private UserService userService;

    @Autowired
    private RestTemplate restTemplate;

    private User testUser;

    @BeforeAll
    void setUp() throws Exception {
        User newUser = new User("Jane Smith", "jane.smith@example.com", "password123", "Manager", true, null, null);
        newUser = userService.createUser(newUser);
        testUser = newUser;
    }

    public User getTestUser() {
        return testUser;
    }

    @Test
    @Order(1)
    void testCreateUser() {
        assertNotNull(testUser);  // Ensure the user was set up
        assertEquals("Jane Smith", testUser.getName());
        assertEquals("jane.smith@example.com", testUser.getEmail());
    }

    @Test
    @Order(2)
    void testGetUserById() {
        User user = userService.getUserById(testUser.getId()).get();
        assertNotNull(user);
    }

    @Test
    @Order(3)
    void testGetAllUsers() {
        List<User> users = userService.getAllUsers();
        assertNotNull(users);
        assertTrue(users.size() > 0);
    }

    @Test
    @Order(5)
    void testUpdateUser() throws Exception {
        // Update an existing user
        User updatedUser = new User("Jane Smith Updated", "jane.smith@example.com", "newpassword123", "Lead Developer", true, null, null);
        updatedUser = userService.updateUser(getTestUser().getId(), updatedUser);

        assertNotNull(updatedUser);
        assertEquals("Jane Smith Updated", updatedUser.getName());
        assertEquals("Lead Developer", updatedUser.getPosition());
    }

    @Test
    @Order(5)
    void testDeleteUser() {
        try {
            userService.deleteUser(getTestUser().getId());
            Optional<User> deletedUser = userService.getUserById(getTestUser().getId());
            assertFalse(deletedUser.isPresent(), "User not exist.");
        } catch (HttpClientErrorException.NotFound ex) {
            assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        }
    }
}
