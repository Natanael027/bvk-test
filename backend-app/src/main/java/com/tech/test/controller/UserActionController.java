package com.tech.test.controller;

import com.tech.test.entity.User;
import com.tech.test.service.CustomerOAuth2UserService;
import com.tech.test.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3001", allowCredentials = "true")
public class UserActionController {
    @Autowired
    private UserService userService;

    @PostMapping("/action/login")
    public ResponseEntity<User> loginUser(@RequestBody User loginRequest, HttpServletRequest request, HttpServletResponse response) {
        // Authenticate the user
        User user = userService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());

        if (user != null && user.isEnabled()) {
            // User is authenticated, create a session manually
            request.getSession(true);  // This will create a session if it doesn't already exist

            // Manually set the JSESSIONID cookie (Spring automatically does this, but you can customize it here)
            Cookie cookie = new Cookie("JSESSIONID", request.getSession().getId());
            cookie.setHttpOnly(true);  // Ensure the cookie is HTTP only for security
            cookie.setSecure(false);   // Set to true if using HTTPS in production
            cookie.setPath("/");       // Make the cookie available across the entire domain
            cookie.setMaxAge(-1);      // Session cookie (it will expire when the session ends)
            response.addCookie(cookie);  // Add the cookie to the response

            return new ResponseEntity<>(user, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/action/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().invalidate();
        Cookie cookie = new Cookie("JSESSIONID", null);  // Set the cookie to null
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        return ResponseEntity.ok("Logged out successfully");
    }

}
