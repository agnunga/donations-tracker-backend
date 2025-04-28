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
    private String paymentBuyGoodsUrl;
    private String paymentBuyGoodsQueueUrl;
    private String paymentBuyGoodsResultUrl;
    private String paymentBuyGoodsCallUrl;
    private String expressUrl;
    private String expressResultUrl;
    private String expressCallUrl;

}
