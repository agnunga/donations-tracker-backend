package io.omosh.dts.config;

import io.omosh.dts.repositories.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;

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
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> userRepository.findByUsername(username)
                .map(user -> User.withUsername(user.getUsername())
                        .password(user.getPassword())
                        .roles(user.getRole().name())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

}