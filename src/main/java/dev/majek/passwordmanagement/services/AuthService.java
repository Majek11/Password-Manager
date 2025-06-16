package dev.majek.passwordmanagement.services;

import dev.majek.passwordmanagement.dto.JwtResponse;
import dev.majek.passwordmanagement.dto.LoginRequest;
import dev.majek.passwordmanagement.dto.SignupRequest;
import dev.majek.passwordmanagement.dto.UserResponse;

public interface AuthService {
    UserResponse register(SignupRequest request);
    JwtResponse login(LoginRequest request);
}
