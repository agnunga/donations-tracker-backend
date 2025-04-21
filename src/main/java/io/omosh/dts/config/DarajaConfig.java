package io.omosh.dts.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "daraja.configs")
public class DarajaConfig {
    private String consumerKey;
    private String consumerSecret;
    private String authUrl;
    private String b2cUrl;
}
