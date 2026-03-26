package com.financetracker.userservice.service;

import com.financetracker.userservice.dto.AuthResponse;
import com.financetracker.userservice.dto.LoginRequest;
import com.financetracker.userservice.dto.RegisterRequest;
import com.financetracker.userservice.entity.User;
import com.financetracker.userservice.repository.UserRepository;
import com.financetracker.userservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,PasswordEncoder passwordEncoder,JwtUtil jwtUtil){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);

        String token = jwtUtil.generateToken(savedUser.getId(), savedUser.getEmail() , savedUser.getName());

        return new AuthResponse(token , savedUser.getName() , savedUser.getEmail(),savedUser.getId() , "Registration successful");
    }

    public AuthResponse login(LoginRequest request){

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("Invalid email and password"));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new RuntimeException("Invalid email and password");
        }

        String token = jwtUtil.generateToken(user.getId() , user.getEmail(), user.getName());

        return new AuthResponse(token , user.getName(), user.getEmail(), user.getId(), "Login successful");
    }
}
