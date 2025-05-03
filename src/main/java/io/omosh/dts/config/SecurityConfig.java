package io.omosh.dts.config;

import io.omosh.dts.repositories.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public WebSessionServerSecurityContextRepository securityContextRepository() {
        return new WebSessionServerSecurityContextRepository();
        // Ensures security context persistence
    }

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http, JwtAuthenticationFilter jwtAuthFilter) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // Disable CSRF protection
                .securityContextRepository(securityContextRepository()) // Ensures SecurityContext persistence
                .authorizeExchange(auth -> auth
                        .pathMatchers("/auth/login/**",
                                "/auth/login-web/**",
                                "/auth/logout/**",
                                "/auth/refresh/**",
                                "/api/donations/**",
                                "/api/campaigns/**",
                                "/api/beneficiaries/**",
                                "/momo/**",
                                "/actuator/**")
                        .permitAll()
                        .pathMatchers(HttpMethod.OPTIONS).permitAll() // Allow preflight requests
                        .pathMatchers("/api/users/**").authenticated()
                        .anyExchange().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public ReactiveUserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> userRepository.findByUsername(username)
                .map(user -> User.withUsername(user.getUsername())
                        .password(user.getPassword())
                        .roles(user.getRole().name())
                        .build())
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")));
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        return new ReactiveAuthenticationManager() {
            @Override
            public Mono<Authentication> authenticate(Authentication authentication) {
                return userDetailsService.findByUsername(authentication.getName())
                        .flatMap(userDetails -> {
                            // Use password encoder to check password
                            if (passwordEncoder.matches(authentication.getCredentials().toString(), userDetails.getPassword())) {
                                // Authentication is successful, wrap in Mono<Authentication>
                                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, authentication.getCredentials(), userDetails.getAuthorities());
                                return Mono.just(auth); // Return Mono<Authentication> explicitly
                            } else {
                                // Password mismatch, return Mono.empty() to indicate authentication failure
                                return Mono.empty();
                            }
                        })
                        .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")));
            }
        };
    }

}