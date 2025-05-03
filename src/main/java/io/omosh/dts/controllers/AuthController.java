package io.omosh.dts.controllers;

import io.omosh.dts.dtos.JwtAccessToken;
import io.omosh.dts.dtos.LoginRequest;
import io.omosh.dts.services.AuthService;
import io.omosh.dts.utils.HelperUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        logger.info("loginRequest :::: {}, \nrequest ::: {}", HelperUtil.toJson(loginRequest), HelperUtil.toJson(request));
        return authService.login(request, loginRequest)
                .map(jwt -> {
                    boolean isSecure = !request.getServerName().equals("localhost");

                    logger.info("after calling authService.login, jwt :::::: {} ", HelperUtil.toJson(jwt));
                    // Set the token as an HttpOnly cookie
                    ResponseCookie tokenCookie = ResponseCookie.from("token", jwt.getToken())
                            .httpOnly(true)
                            .secure(isSecure)
                            .path("/")
                            .maxAge(Duration.ofMinutes(15)) // Access token lifespan
                            .build();

                    // Return refresh token in the response body to store in client-side cookie
                    JwtAccessToken jwtAccessToken = new JwtAccessToken(jwt.getRefreshToken(), jwt.getToken(),
                            jwt.getSub(), jwt.getIat(), jwt.getJti(), jwt.getExp(), jwt.getRole());

                    return ResponseEntity.ok()
                            .header(HttpHeaders.SET_COOKIE, tokenCookie.toString())
                            .body(jwtAccessToken); // Include refresh-token in response
                })
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new JwtAccessToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtAccessToken> refresh(HttpServletRequest request, @RequestHeader(value = "Refresh-Token", required = false) String headerToken) {
        logger.info("just arrived, refresh ::: ");
        String token = null;
        if (headerToken != null) {
            token = headerToken;
        } else {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("refreshtoken".equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }
        }

        if (token != null) {
            return authService.refreshToken(token)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new JwtAccessToken()));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new JwtAccessToken());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(value = "Refresh-Token", required = false) String headerToken, @CookieValue(value = "refreshToken", required = false) String cookieToken) {
        logger.info("just arrived, logout ::: ");
        String refreshToken = headerToken != null ? headerToken : cookieToken;

        if (refreshToken == null) {
            return ResponseEntity.badRequest().build();
        }

        authService.logout(refreshToken);

        // Always remove the cookie on logout
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .path("/")
                .httpOnly(true)
                .secure(true)
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }

}