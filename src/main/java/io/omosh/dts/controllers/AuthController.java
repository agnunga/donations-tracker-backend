package io.omosh.dts.controllers;

import io.omosh.dts.dtos.JwtAccessToken;
import io.omosh.dts.dtos.LoginRequest;
import io.omosh.dts.services.AuthService;
import io.omosh.dts.utils.HelperUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;

import java.time.Duration;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAccessToken> login(HttpServletRequest request, @RequestBody LoginRequest loginRequest) {
        return authService.login(request, loginRequest)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new JwtAccessToken("none", "0")));
    }


    @PostMapping("/login-web")
    public ResponseEntity<JwtAccessToken> login2(HttpServletRequest request, @RequestBody LoginRequest loginRequest) {
        logger.info("loginRequest :::: {}, \nexchange ::: {}", HelperUtil.toJson(loginRequest), HelperUtil.toJson(request));
        return authService.login(request, loginRequest)
                .map(jwt -> {
                    // Create the refresh token cookie
                    logger.info("Generated refresh token: {}", jwt.getToken());
                    ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", jwt.getToken())
                            .httpOnly(true)
                            .secure(true) // Set based on your environment
                            .path("/")     // Available throughout the app
                            .maxAge(Duration.ofDays(7)) // Or any refresh token lifespan
                            .build();

                    return ResponseEntity.ok()
                            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                            .body(jwt);
                })
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new JwtAccessToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtAccessToken> refresh(@RequestHeader("Refresh-Token") String refreshToken) {
        logger.info("refreshToken ::: {}", refreshToken);
        return authService.refreshToken(refreshToken)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new JwtAccessToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<JwtAccessToken> logout(@RequestHeader("Refresh-Token") String refreshToken) {
        return authService.logout(refreshToken)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(null));
    }
}