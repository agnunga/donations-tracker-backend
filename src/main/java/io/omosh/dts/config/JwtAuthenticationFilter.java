package io.omosh.dts.config;

import io.omosh.dts.repositories.UserRepository;
import io.omosh.dts.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
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

@Component
public class JwtAuthenticationFilter implements WebFilter {

    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        String userAgent = request.getHeaders().getFirst("User-Agent");
        String clientIp = request.getHeaders().getFirst("X-Forwarded-For");

        if (clientIp == null && request.getRemoteAddress() != null) {
            clientIp = request.getRemoteAddress().getAddress().getHostAddress();
        }
        logger.info("Client IP: {}, User-Agent: {}", clientIp, userAgent);

        String token = extractToken(exchange);
        logger.info("JwtAuthenticationFilter triggered token ::: {}", token);

        if (token != null) {
            logger.info("Extracted token: {}", token);

            return JwtUtil.extractUsername(token)
                    .flatMap(username -> {
                        logger.info("Extracted username from token: {}", username);

                        return userRepository.findByUsername(username)
                                .flatMap(user -> JwtUtil.isTokenValid(token, username)
                                        .filter(Boolean::booleanValue) // proceed only if token is valid
                                        .map(valid -> user)
                                )
                                .flatMap(user -> {
                                    UserDetails userDetails = org.springframework.security.core.userdetails.User
                                            .withUsername(user.getUsername())
                                            .password(user.getPassword())
                                            .roles(user.getRole().name())
                                            .build();

                                    UsernamePasswordAuthenticationToken authToken =
                                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                                    context.setAuthentication(authToken);

                                    logger.info("User authenticated: {}", username);

                                    return chain.filter(exchange)
                                            .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
                                });

                    })
                    .switchIfEmpty(chain.filter(exchange));
        } else {
            logger.warn("No JWT token found in request");
            return chain.filter(exchange);
        }

    }

    private String extractToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        logger.info("extractToken ::: {}", authHeader);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
