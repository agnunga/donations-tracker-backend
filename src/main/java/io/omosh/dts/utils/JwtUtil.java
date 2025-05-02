package io.omosh.dts.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.omosh.dts.dtos.JwtAccessToken;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

public class JwtUtil {
    private static final String SECRET_KEY = "0N9p18iTq0EKfGcgoyD9KCKDbZUqgxbFAlxs4CPyK0A="; // 32+ chars
    private static final SecretKey SIGNING_KEY = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET_KEY));
    private static final long EXPIRATION_TIME = 3600 - 1; // 1 hour

    /**
     * Generates a JWT token.
     */
    public static JwtAccessToken generateToken(String username, Map<String, Object> extraClaims) {
        String token = Jwts.builder()
                .claims(extraClaims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME * 1000))
                .signWith(SIGNING_KEY)
                .compact();
        return new JwtAccessToken(token, EXPIRATION_TIME);
    }

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
}
