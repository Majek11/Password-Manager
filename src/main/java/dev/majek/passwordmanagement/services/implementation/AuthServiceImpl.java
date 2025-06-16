package dev.majek.passwordmanagement.services.implementation;

import dev.majek.passwordmanagement.domain.model.User;
import dev.majek.passwordmanagement.domain.repository.UserRepository;
import dev.majek.passwordmanagement.dto.JwtResponse;
import dev.majek.passwordmanagement.dto.LoginRequest;
import dev.majek.passwordmanagement.dto.SignupRequest;
import dev.majek.passwordmanagement.dto.UserResponse;
import dev.majek.passwordmanagement.exception.ResourceAlreadyExistsException;
import dev.majek.passwordmanagement.security.JwtTokenProvider;
import dev.majek.passwordmanagement.services.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public UserResponse register(SignupRequest request) {
        log.debug("Starting user registration for email: {}", request.getEmail());

        userRepository.findByEmail(request.getEmail())
                .ifPresent(user -> {
                    log.error("User with email {} already exists", request.getEmail());
                    throw new ResourceAlreadyExistsException("User with email " + request.getEmail() + " already exists");
                });

        User user = new User();
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setMasterPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setVerified(false);

        log.debug("Saving new user to database");
        User savedUser = userRepository.save(user);
        log.debug("User successfully saved with ID: {}", savedUser.getId());

        return new UserResponse(savedUser.getId(), savedUser.getFullName(), savedUser.getEmail());
    }

    @Override
    public JwtResponse login(LoginRequest request) {
        log.debug("Processing login request for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getMasterPasswordHash())) {
            log.error("Invalid password attempt for user: {}", request.getEmail());
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtTokenProvider.generateToken(user);
        log.debug("Login successful for user: {}", request.getEmail());

        return new JwtResponse(token, user.getId(), user.getFullName(), user.getEmail());
    }
}