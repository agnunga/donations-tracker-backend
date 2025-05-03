package io.omosh.dts.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.omosh.dts.dtos.JwtAccessToken;
import io.omosh.dts.models.User;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.*;

public class JwtUtil {

    private static final String SECRET_KEY = "0N9p18iTq0EKfGcgoyD9KCKDbZUqgxbFAlxs4CPyK0A="; // 32+ chars
    private static final SecretKey SIGNING_KEY = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET_KEY));
    private static final long TOKEN_EXPIRATION_TIME_MS = 15 * 60 * 1000; // 15 minutes
    private static final long REFRESH_EXPIRATION_TIME_MS = 7 * 24 * 60 * 60 * 1000; // 7 days

    public static String generateRefreshToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_TIME_MS))
                .signWith(SIGNING_KEY)
                .compact();
    }

    public static JwtAccessToken generateToken(User user) {
        long iat = System.currentTimeMillis();
        long exp = System.currentTimeMillis() + TOKEN_EXPIRATION_TIME_MS * 1000;

        // Generate access token
        String accessToken = Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date(iat))
                .expiration(new Date(exp))
                .claim("role", user.getRole().name())
                .claim("jti", UUID.randomUUID().toString())
                .signWith(SIGNING_KEY)
                .compact();

        // Generate refresh token
        String refreshToken = UUID.randomUUID().toString();

        // Return both tokens in the response DTO
        return new JwtAccessToken(refreshToken, accessToken, user.getUsername(), String.valueOf(iat), UUID.randomUUID().toString(), String.valueOf(exp), user.getRole().name());
    }

    public static JwtAccessToken generateToken2(User user) {
        long now = System.currentTimeMillis();
        long exp = now + TOKEN_EXPIRATION_TIME_MS * 1000;

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());
        claims.put("jti", HelperUtil.generateUUID());

        String token = Jwts.builder()
                .claims(claims)
                .subject(user.getUsername())
                .issuedAt(new Date(now))
                .expiration(new Date(exp))
                .signWith(SIGNING_KEY)
                .compact();

        String refreshToken = (String) claims.get("jti") + HelperUtil.generateUUID();
        return new JwtAccessToken(refreshToken, token, user.getUsername(), String.valueOf(now),
                (String) claims.get("jti"), String.valueOf(exp), user.getRole().name());
    }

    /**
     * Extracts claims from a JWT token.
     */
    public static Mono<Claims> extractClaims(String token) {
        try {
            return Mono.just(Jwts.parser()
                    .verifyWith(SIGNING_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload());
        } catch (Exception e) {
            return Mono.empty();
        }
    }

    /**
     * Extracts the username (subject) from a JWT token.
     */
    public static Mono<String> extractUsername(String token) {
        return extractClaims(token).map(Claims::getSubject);
    }

    /**
     * Checks if the token is expired.
     */
    private static Mono<Boolean> isTokenExpired(String token) {
        return extractClaims(token)
                .map(claims -> claims.getExpiration().before(new Date()))
                .defaultIfEmpty(true);
    }

    /**
     * Validates the token.
     */
    public static Mono<Boolean> isTokenValid(String token, String username) {
        return extractUsername(token)
                .filter(extractedUsername -> extractedUsername.equals(username))
                .flatMap(extractedUsername ->
                        isTokenExpired(token).map(expired -> !expired)
                )
                .defaultIfEmpty(false);
    }
}
