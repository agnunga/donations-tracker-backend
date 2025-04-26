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
    private String c2bRegisterCallUrl;
    private String c2bShortCode;
    private String c2bConfirmationUrl;
    private String c2bValidationUrl;
    private String c2bSimulateUrl;
    private String queryTransactionUrl;
    private String queryTransactionCallUrl;
    private String queryTransactionQueueTimeoutUrl;
    private String queryTransactionResult;
    private String queryBalanceUrl;
    private String queryBalQueueTimeoutURL;
    private String queryBalResultURL;
    private String queryBalCallURL;

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

    public String getFullC2bRegisterCallUrl() {
        return getBaseUrl() + getC2bRegisterCallUrl();
    }

    public String getFullQueryTransactionCallUrl() {
        return getBaseUrl() + getQueryTransactionCallUrl();
    }

    public String getFullQueryTransactionQueueTimeoutUrl() {
        return getBaseUrl() + getQueryTransactionQueueTimeoutUrl();
    }

    public String getFullQueryTransactionResult() {
        return getBaseUrl() + getQueryTransactionResult();
    }

    public String getFullQueryBalQueueTimeoutURL() {
        return getBaseUrl() + getQueryBalQueueTimeoutURL();
    }

    public String getFullQueryBalResultURL() {
        return getBaseUrl() + getQueryBalResultURL();
    }

    public String getFullQueryBalCallURL() {
        return getBaseUrl() + getQueryBalCallURL();
    }

}
