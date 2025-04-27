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
    private String c2bRegisterUrlUrl;
    private String c2bRegisterCallUrl;
    private String c2bShortCode;
    private String c2bConfirmationUrl;
    private String c2bValidationUrl;
    private String c2bSimulateUrl;
    private String queryTransactionUrl;
    private String queryTransactionCallUrl;
    private String queryTransactionQueueUrl;
    private String queryTransactionResult;
    private String queryBalUrl;
    private String queryBalQueueUrl;
    private String queryBalResultUrl;
    private String queryBalCallUrl;
    private String reversalUrl;
    private String reversalQueueUrl;
    private String reversalResultUrl;
    private String reversalCallUrl;
    private String remitTaxUrl;
    private String remitTaxQueueUrl;
    private String remitTaxResultUrl;
    private String remitTaxCallUrl;
    private String paymentRequestUrl;
    private String paymentRequestQueueUrl;
    private String paymentRequestResultUrl;
    private String paymentRequestCallUrl;

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

    public String getFullQueryTransactionQueueUrl() {
        return getBaseUrl() + getQueryTransactionQueueUrl();
    }

    public String getFullQueryTransactionResult() {
        return getBaseUrl() + getQueryTransactionResult();
    }

    public String getFullQueryBalQueueUrl() {
        return getBaseUrl() + getQueryBalQueueUrl();
    }

    public String getFullQueryBalResultUrl() {
        return getBaseUrl() + getQueryBalResultUrl();
    }

    public String getFullQueryBalCallURL() {
        return getBaseUrl() + getQueryBalCallUrl();
    }

    public String getFullReversalQueueUrl() {
        return getBaseUrl() + getReversalQueueUrl();
    }

    public String getFullReversalResultUrl() {
        return getBaseUrl() + getReversalResultUrl();
    }

    public String getFullReversalCallURL() {
        return getBaseUrl() + getReversalCallUrl();
    }

    public String getFullRemitTaxQueueUrl() {
        return getBaseUrl() + getRemitTaxQueueUrl();
    }

    public String getFullRemitTaxResultUrl() {
        return getBaseUrl() + getRemitTaxResultUrl();
    }

    public String getFullRemitTaxCallURL() {
        return getBaseUrl() + getRemitTaxCallUrl();
    }

    public String getFullPaymentRequestQueueUrl() {
        return getBaseUrl() + getPaymentRequestQueueUrl();
    }

    public String getFullPaymentRequestResultUrl() {
        return getBaseUrl() + getPaymentRequestResultUrl();
    }

    public String getFullPaymentRequestCallURL() {
        return getBaseUrl() + getPaymentRequestCallUrl();
    }

}
