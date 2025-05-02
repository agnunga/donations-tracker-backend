package io.omosh.dts.services;
import io.omosh.dts.dtos.JwtAccessToken;
import io.omosh.dts.utils.JwtUtil;
import io.omosh.dts.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<JwtAccessToken> login(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .map(user -> JwtUtil.generateToken(username, new HashMap<>()));
    }

    public boolean validateToken(String token) {
        Optional<String> usernameOpt = JwtUtil.extractUsername(token);

        return usernameOpt.map(s -> userRepository.findByUsername(s)
                .map(user -> JwtUtil.isTokenValid(token, user.getUsername()))
                .orElse(false)).orElse(false);

    }
}
