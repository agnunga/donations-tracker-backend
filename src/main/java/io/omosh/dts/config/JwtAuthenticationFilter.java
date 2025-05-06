package io.omosh.dts.config;

import io.omosh.dts.models.User;
import io.omosh.dts.repositories.UserRepository;
import io.omosh.dts.utils.HelperUtil;
import io.omosh.dts.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
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
        logger.info("JwtAuthenticationFilter triggered, URI: {}", request.getRequestURI());

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
                                .password(user.getPassword())
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

                        filterChain.doFilter(request, response);
                        return;
                    }
                }

                // If username couldn't be extracted or token is invalid
                logger.info("Authentication failed: Invalid or expired token");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Invalid or expired token\"}");
                return; // ✅ STOP further processing
            } else {
                logger.info("No JWT token found in Authorization header");
            }
        } catch (Exception e) {
            logger.error("Authentication filter error", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Internal error in authentication filter\"}");
            return; // ✅ STOP on unexpected error
        }

        filterChain.doFilter(request, response);
    }


    private String extractToken(HttpServletRequest request) {
        logger.info("extractToken -> entering before checking authHeader");
        // 1. Check Authorization header
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            logger.info("extractToken -> getHeader :::: {}", authHeader);
            return authHeader.substring(7);
        }

        logger.info("extractToken -> no authHeader {}", HelperUtil.toJson(request.getCookies()));
        // 2. If not in header, check cookies
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("token".equals(cookie.getName())) {
                    logger.info("extractToken -> cookie.getValue(); :::: {}", cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
