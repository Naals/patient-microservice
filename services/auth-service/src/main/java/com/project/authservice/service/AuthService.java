package com.project.authservice.service;

import com.project.authservice.dto.login.LoginRequestDTO;
import com.project.authservice.service.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public Optional<String> authenticate(LoginRequestDTO loginRequestDTO) {
        return userService.findByEmail(loginRequestDTO.getEmail())
                .filter(u-> passwordEncoder.matches(
                        loginRequestDTO.getPassword(),
                        u.getPassword()
                ))
                .map(u-> jwtUtil.generateToken(u.getEmail(), u.getRole()));
    }

    public Optional<String> register(String email, String rawPassword) {

        if (userService.existsByEmail(email)) {
            return Optional.empty();
        }

        String encodedPassword = passwordEncoder.encode(rawPassword);

        userService.createUser(email, encodedPassword);

        return Optional.of(
                jwtUtil.generateToken(email, "USER")
        );
    }

    public boolean validateToken( String token ){
        try{
            jwtUtil.validateToken(token);
            return true;
        } catch (JwtException e){
            return false;
        }
    }
}
