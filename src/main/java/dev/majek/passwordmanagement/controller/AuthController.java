package dev.majek.passwordmanagement.controller;

import dev.majek.passwordmanagement.dto.JwtResponse;
import dev.majek.passwordmanagement.dto.LoginRequest;
import dev.majek.passwordmanagement.dto.SignupRequest;
import dev.majek.passwordmanagement.dto.UserResponse;
import dev.majek.passwordmanagement.services.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/auth", produces = "application/json")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @PostMapping(value = "/signup", consumes = "application/json", produces = "application/json")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody SignupRequest request) {
        try {
            logger.debug("Received signup request for email: {}", request.getEmail());
            UserResponse response = authService.register(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error during registration: ", e);
            throw e;
        }
    }



    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        JwtResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
