package io.omosh.dts.config;

import io.omosh.dts.models.User;
import io.omosh.dts.repositories.UserRepository;
import io.omosh.dts.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * Custom JWT Authentication Filter for Spring MVC.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final UserRepository userRepository; // ✅ make sure you import your correct repository

    public JwtAuthenticationFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String token = extractToken(request);
            logger.info("JwtAuthenticationFilter triggered, token: {}", token);

            if (token != null) {
                Optional<String> usernameOpt = JwtUtil.extractUsername(token);

                if (usernameOpt.isPresent()) {
                    String username = usernameOpt.get();
                    logger.info("Extracted username from token: {}", username);

                    Optional<User> userOpt = userRepository.findByUsername(username);
                    if (userOpt.isPresent() && JwtUtil.isTokenValid(token, username)) {
                        User user = userOpt.get();

                        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                                .username(user.getUsername())
                                .password(user.getPassword()) // Optional for stateless JWTs
                                .roles(user.getRole().name())
                                .build();

                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );

                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        logger.info("User authenticated: {}", username);
                    } else {
                        logger.info("Authentication failed: Invalid or expired token");
                    }
                }
                logger.info("Token exists, but cannot extract username from token");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid or expired token");
//                return;
            } else {
                logger.info("No JWT token found in Authorization header");
            }
        } catch (Exception e) {
            logger.info("Authentication filter error", e);
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
