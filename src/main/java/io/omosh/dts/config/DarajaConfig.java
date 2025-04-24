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
    private String securityCredential;
    private String authUrl;
    private String baseUrl;
    private String b2cUrl;
    private String b2cPartyA;
    private String b2cInitiatorName;
    private String b2cInitiatorPassword;
    private String b2cInitiateUrl;
    private String b2cCallbackUrl;
    private String b2cResultUrl;
    private String c2bRegisterUrl;
    private String c2bShortCode;
    private String c2bConfirmationUrl;
    private String c2bValidationUrl;
    private String c2bSimulateUrl;

    public String getFullB2cInitiateUrl() {
        return getBaseUrl() + getB2cInitiateUrl();
    }

    public String getFullB2cCallbackUrl() {
        return getBaseUrl() + getB2cCallbackUrl();
    }

    public String getFullB2cResultUrl() {
        return getBaseUrl() + getB2cResultUrl();
    }

    public String getFullC2bConfirmationUrl() {
        return getBaseUrl() + getC2bConfirmationUrl();
    }

    public String getFullC2bValidationUrl() {
        return getBaseUrl() + getC2bValidationUrl();
    }

}
