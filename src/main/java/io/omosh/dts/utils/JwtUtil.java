package io.omosh.dts.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.omosh.dts.dtos.JwtAccessToken;
import io.omosh.dts.models.User;

import javax.crypto.SecretKey;
import java.util.*;

public class JwtUtil {
    private static final String SECRET_KEY = "0N9p18iTq0EKfGcgoyD9KCKDbZUqgxbFAlxs4CPyK0A="; // 32+ chars
    private static final SecretKey SIGNING_KEY = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET_KEY));
    private static final long TOKEN_EXPIRATION_TIME_MS = 15 * 60 * 1000; // 15 minutes
    private static final long REFRESH_EXPIRATION_TIME_MS = 7 * 24 * 60 * 60 * 1000; // 7 days

    /**
     * Extracts claims from a JWT token.
     */
    public static Optional<Claims> extractClaims(String token) {
        try {
            return Optional.of(Jwts.parser()
                    .verifyWith(SIGNING_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Extracts the username (subject) from a JWT token.
     */
    public static Optional<String> extractUsername(String token) {
        return extractClaims(token).map(Claims::getSubject);
    }

    /**
     * Checks if the token is expired.
     */
    private static boolean isTokenExpired(String token) {
        return extractClaims(token)
                .map(claims -> claims.getExpiration().before(new Date()))
                .orElse(true);
    }

    /**
     * Validates the token.
     */
    public static boolean isTokenValid(String token, String username) {
        return extractUsername(token)
                .filter(extractedUsername -> extractedUsername.equals(username) && !isTokenExpired(token))
                .isPresent();
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

}
