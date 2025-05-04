package io.omosh.dts.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        // Specify allowed origins
        corsConfig.setAllowedOrigins(Arrays.asList("http://localhost:3000", "https://donations-tracker-three.vercel.app"));
        // Specify allowed HTTP methods
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Specify allowed headers
        corsConfig.setAllowedHeaders(Arrays.asList("Refresh-Token", "Cache-Control", "Authorization", "Content-Type", "Accept", "ngrok-skip-browser-warning"));
        // Specify exposed headers
        corsConfig.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        // Allow credentials
        corsConfig.setAllowCredentials(true);
        // Set max age
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsFilter(source);
    }
}
