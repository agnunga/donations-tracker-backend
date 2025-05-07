package io.omosh.dts.controllers;

import io.omosh.dts.dtos.JwtAccessToken;
import io.omosh.dts.dtos.LoginRequest;
import io.omosh.dts.services.AuthService;
import io.omosh.dts.utils.HelperUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login-")
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
                            .sameSite("None")
                            .build();

                    ResponseCookie refreshCookie = ResponseCookie.from("refreshtoken", jwt.getRefreshToken())
                            .httpOnly(true)
                            .secure(isSecure)
                            .path("/auth/refresh") // Only sent to the refresh endpoint
                            .maxAge(Duration.ofDays(7))
                            .sameSite("None")//.sameSite("Strict")
                            .build();

                    // Return refresh token in the response body to store in client-side cookie
                    JwtAccessToken jwtAccessToken = new JwtAccessToken();
                    jwtAccessToken.setSub(jwt.getSub());
                    jwtAccessToken.setRole(jwt.getRole());

                    return ResponseEntity.ok()
                            .header(HttpHeaders.SET_COOKIE, tokenCookie.toString())
                            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                            .body(jwtAccessToken); // has only sub and role
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

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkAuthentication() {
        logger.info("checkAuthentication:::");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user is authenticated (and not anonymous)
        boolean isAuthenticated = authentication != null &&
                authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken);

        logger.info("checkAuthentication::: isAuthenticated ::: {} ", isAuthenticated);

        // Return the status with appropriate HTTP status codes
        if (isAuthenticated) {
            return ResponseEntity.ok(true); // 200 OK with true
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false); // 401 Unauthorized with false
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request, HttpServletResponse response) {
        logger.info("logout triggered");

        boolean isSecure = !request.getServerName().equals("localhost");

        // Invalidate session if it exists
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Expire the 'token' cookie
        ResponseCookie tokenCookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(isSecure)
                .path("/")
                .maxAge(0)
                .sameSite("None")
                .build();

        // Expire the 'refreshtoken' cookie
        ResponseCookie refreshCookie = ResponseCookie.from("refreshtoken", "")
                .httpOnly(true)
                .secure(isSecure)
                .path("/auth/refresh")
                .maxAge(0)
                .sameSite("None")
                .build();

        // Clear security context
        SecurityContextHolder.clearContext();

        // Response body
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("success", true);
        responseBody.put("message", "Logged out successfully");

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, tokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(responseBody);
    }

    @PostMapping("/logout2")
    public ResponseEntity<Void> logout2(@RequestHeader(value = "Refresh-Token", required = false) String headerToken, @CookieValue(value = "refreshToken", required = false) String cookieToken) {
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