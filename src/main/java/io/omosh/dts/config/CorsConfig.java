package io.omosh.dts.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        // Specify allowed origins
        corsConfig.setAllowedOrigins(Arrays.asList("http://localhost:3000", "https://donations-tracker-three.vercel.app"));
        // Specify allowed HTTP methods
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Specify allowed headers
        corsConfig.setAllowedHeaders(Arrays.asList("Cache-Control", "Authorization", "Content-Type", "Accept", "ngrok-skip-browser-warning"));
        // Specify exposed headers
        corsConfig.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        // Allow credentials
        corsConfig.setAllowCredentials(true);
        // Set max age
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }


}