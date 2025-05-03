package io.omosh.dts.services;

import io.omosh.dts.dtos.JwtAccessToken;
import io.omosh.dts.dtos.LoginRequest;
import io.omosh.dts.models.RefreshTokenRecord;
import io.omosh.dts.repositories.RefreshTokenRepository;
import io.omosh.dts.utils.JwtUtil;
import io.omosh.dts.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    Logger logger = LoggerFactory.getLogger(AuthService.class);

    public AuthService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Mono<JwtAccessToken> login(ServerWebExchange exchange, LoginRequest loginRequest) {
        return userRepository.findByUsername(loginRequest.getUsername())
                .filter(user -> passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()))
                .map(user -> saveRefreshTokenRecord(exchange, JwtUtil.generateToken(user)));
    }

    public JwtAccessToken saveRefreshTokenRecord(ServerWebExchange exchange, JwtAccessToken jwtAccessToken) {
        // Create the refresh token record
        String clientIp = exchange.getAttribute("clientIp");
        String userAgent = exchange.getAttribute("userAgent");

        RefreshTokenRecord refreshTokenRecord = new RefreshTokenRecord(
                jwtAccessToken.getRefreshToken(),
                jwtAccessToken.getSub(),
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7), // Set expiration (7 days example)
                clientIp, // Optionally store user agent here
                userAgent  // Optionally store IP address here
        );

        // Save the refresh token record to the database
        refreshTokenRepository.save(refreshTokenRecord);
        return jwtAccessToken;
    }

    public void revokeRefreshToken(String refreshToken) {
        refreshTokenRepository.findByRefreshToken(refreshToken)
                .flatMap(record -> {
                    record.revoke(); // mark it as revoked
                    return refreshTokenRepository.save(record); // save the updated record
                })
                .subscribe();
    }

    public Mono<JwtAccessToken> refreshToken(String refreshToken) {
        logger.info("refreshToken ::: {}", refreshToken);

        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .filter(record -> !record.isExpired() && !record.isRevoked())
                .flatMap(validRecord ->
                        userRepository.findByUsername(validRecord.getUsername())
                                .map(user -> {
                                    JwtAccessToken newAccessToken = JwtUtil.generateToken(user);
                                    newAccessToken.setRefreshToken(refreshToken); // reuse refresh token
                                    return newAccessToken;
                                })
                );
    }

    public Mono<JwtAccessToken> logout(String refreshToken) {
        revokeRefreshToken(refreshToken);
        return Mono.empty();
    }
}
