package io.omosh.dts.services;

import io.omosh.dts.dtos.JwtAccessToken;
import io.omosh.dts.dtos.LoginRequest;
import io.omosh.dts.dtos.UserDTO;
import io.omosh.dts.models.RefreshTokenRecord;
import io.omosh.dts.models.User;
import io.omosh.dts.repositories.RefreshTokenRepository;
import io.omosh.dts.utils.JwtUtil;
import io.omosh.dts.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    Logger logger = LoggerFactory.getLogger(AuthService.class);

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenRepository = refreshTokenRepository;
    }


    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<JwtAccessToken> login(HttpServletRequest request, LoginRequest loginRequest) {
        return findUserByUsername(loginRequest.username())
                .filter(user -> passwordEncoder.matches(loginRequest.password(), user.getPassword()))
                .map(user -> saveRefreshTokenRecord(request, JwtUtil.generateToken(user)));
    }

    public boolean validateToken(String token) {
        Optional<String> usernameOpt = JwtUtil.extractUsername(token);

        return usernameOpt.map(s -> userRepository.findByUsername(s)
                .map(user -> JwtUtil.isTokenValid(token, user.getUsername()))
                .orElse(false)).orElse(false);

    }

    public JwtAccessToken saveRefreshTokenRecord(HttpServletRequest request, JwtAccessToken jwtAccessToken) {
        // Create the refresh token record
        String userAgent = request.getHeader("User-Agent");

        // Try to get the real client IP address behind proxy/load balancer
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr(); // fallback to direct remote address
        }

        RefreshTokenRecord refreshTokenRecord = new RefreshTokenRecord(
                jwtAccessToken.getRefreshToken(),
                jwtAccessToken.getSub(),
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7), // Set expiration (7 days example)
                ipAddress, // Optionally store user agent here
                userAgent  // Optionally store IP address here
        );

        // Save the refresh token record to the database
        refreshTokenRepository.save(refreshTokenRecord);
        return jwtAccessToken;
    }

    public Optional<RefreshTokenRecord> revokeRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .map(record -> {
                    record.revoke(); // mark it as revoked
                    return refreshTokenRepository.save(record); // save the updated record
                });
    }

    public Optional<RefreshTokenRecord> findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshTokenAndRevokedFalseAndExpirationAfter(refreshToken, LocalDateTime.now());
    }

}
