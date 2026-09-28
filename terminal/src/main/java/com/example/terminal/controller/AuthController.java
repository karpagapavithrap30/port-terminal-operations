package com.example.terminal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.terminal.model.User;
import com.example.terminal.repository.UserRepository;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");

        Map<String, Object> response = new HashMap<>();

        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Username and password are required.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Optional<User> userOpt = userRepository.findByUsername(username.trim());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(password.trim())) {
                response.put("success", true);
                response.put("message", "Login successful");
                
                // Return user profile payload without sensitive password
                Map<String, Object> userProfile = new HashMap<>();
                userProfile.put("id", user.getId());
                userProfile.put("username", user.getUsername());
                userProfile.put("fullName", user.getFullName());
                userProfile.put("email", user.getEmail());
                userProfile.put("role", user.getRole());
                userProfile.put("department", user.getDepartment());
                
                response.put("user", userProfile);
                return ResponseEntity.ok(response);
            }
        }

        response.put("success", false);
        response.put("message", "Invalid username or password");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User newUser) {
        Map<String, Object> response = new HashMap<>();

        if (newUser.getUsername() == null || newUser.getUsername().trim().isEmpty() ||
            newUser.getPassword() == null || newUser.getPassword().trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Username and password cannot be empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if (userRepository.existsByUsername(newUser.getUsername().trim())) {
            response.put("success", false);
            response.put("message", "Username is already registered");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        if (newUser.getRole() == null || newUser.getRole().isEmpty()) {
            newUser.setRole("YARD_MANAGER");
        }
        if (newUser.getDepartment() == null || newUser.getDepartment().isEmpty()) {
            newUser.setDepartment("Operations");
        }

        User savedUser = userRepository.save(newUser);
        
        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("id", savedUser.getId());
        userProfile.put("username", savedUser.getUsername());
        userProfile.put("fullName", savedUser.getFullName());
        userProfile.put("email", savedUser.getEmail());
        userProfile.put("role", savedUser.getRole());
        userProfile.put("department", savedUser.getDepartment());

        response.put("success", true);
        response.put("message", "User account registered successfully");
        response.put("user", userProfile);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        // Clear passwords before returning list
        users.forEach(u -> u.setPassword("[PROTECTED]"));
        return ResponseEntity.ok(users);
    }
}
