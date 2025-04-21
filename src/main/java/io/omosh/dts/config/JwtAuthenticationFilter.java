package io.omosh.dts.config;

import io.omosh.dts.models.User;
import io.omosh.dts.repositories.UserRepository;
import io.omosh.dts.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = extractToken(exchange);
        logger.info("JwtAuthenticationFilter triggered token ::: {}", token);

        if (token != null) {
            Optional<String> usernameOpt = JwtUtil.extractUsername(token);
            logger.info("Extracted token: {}", token);

            if (usernameOpt.isPresent()) {
                String username = usernameOpt.get();

                logger.info("Extracted username from token: {}", username);

                // Fetch user from database
                Optional<User> userOpt = userRepository.findByUsername(username);
                if (userOpt.isPresent() && JwtUtil.isTokenValid(token, username)) {
                    User user = userOpt.get();

                    UserDetails userDetails = org.springframework.security.core.userdetails.User
                            .withUsername(user.getUsername())
                            .password(user.getPassword()) // Not needed for security checks
                            .roles(user.getRole().name())
                            .build();

                    // Create authentication object
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());


                    logger.info("User authenticated: {}", username);
                    // After creating authToken
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(authToken);
                    logger.info("SecurityContext Authentication: {}", SecurityContextHolder.getContext().getAuthentication());
                    return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));

                }else{
                    logger.error("User not authenticated: Toked expired");
                }
            }
        }else {
            logger.warn("NO JWT token found in request");
        }

        return chain.filter(exchange);
    }

    private String extractToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        logger.info("extractToken ::: {}", authHeader );
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
