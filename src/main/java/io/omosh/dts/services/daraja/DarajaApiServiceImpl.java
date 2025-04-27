package io.omosh.dts.services.daraja;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.omosh.dts.config.DarajaConfig;
import io.omosh.dts.dtos.daraja.*;
import io.omosh.dts.utils.HelperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
public class DarajaApiServiceImpl implements DarajaApiService {

    private final DarajaConfig darajaConfig;
    private final WebClient webClient;
    ObjectMapper objectMapper;
    Logger logger = LoggerFactory.getLogger(DarajaApiServiceImpl.class);

    private AccessTokenResponse cachedToken;
    private Instant expiryTime;
    private final String securityCredential;

    @Autowired
    public DarajaApiServiceImpl(DarajaConfig darajaConfig, WebClient.Builder webClientBuilder) {
        this.darajaConfig = darajaConfig;
        this.webClient = webClientBuilder.build();
        objectMapper = new ObjectMapper();
        this.securityCredential = HelperUtil.encryptPassword(darajaConfig.getB2cInitiatorPassword());
    }

    public synchronized Mono<String> getValidAccessToken() {
        if (cachedToken != null && Instant.now().isBefore(expiryTime)) {
            return Mono.just(cachedToken.getAccessToken());
        }

        return getAccessToken()
                .doOnNext(token -> {
                    this.cachedToken = token;
                    // Set expiry 1 minute earlier for buffer
                    logger.info("cachedToken :::::: {}", cachedToken);
                    this.expiryTime = Instant.now().plusSeconds(59 * 60 - 60);
                })
                .map(AccessTokenResponse::getAccessToken);
    }

    @Override
    public Mono<AccessTokenResponse> getAccessToken() {
        try {
            String authHeader = HelperUtil.toBase64(
                    darajaConfig.getConsumerKey() + ":" + darajaConfig.getConsumerSecret()
            );

            logger.info("The auth header :: {}", authHeader);
            logger.info("The auth URL :: {}", darajaConfig.getAuthUrl());

            return webClient.get()
                    .uri(darajaConfig.getAuthUrl())
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + authHeader)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(AccessTokenResponse.class)
                    .doOnError(e -> logger.error("Failed to fetch access token::{}", e.getMessage()));

        } catch (Exception e) {
            // Wrap checked exception (e.g., Base64 encoding) in a Mono error
            return Mono.error(new RuntimeException("Error creating auth header::: {}", e));
        }
    }

    @Override
    public Mono<SyncResponse> getB2CTransactionResults(B2cResponse b2CResponse) {
        logger.info("b2CResponse ::: {}", HelperUtil.toJson(b2CResponse));
        return null;
    }

    @Override
    public Mono<SyncResponse> performB2CTransaction(B2cRequestExternal b2CRequestExternal) {
        logger.info("Service performB2CTransaction :::");
        B2cRequest b2CRequest = getB2cRequest(b2CRequestExternal);
        return callPost(darajaConfig.getB2cUrl(), b2CRequest);
    }

    @Override
    public Mono<SyncResponse> c2bRegisterUrl() {
        logger.info("Service c2bRegisterUrl :::");
        C2bRegisterUrl c2bRegister = getC2bRegisterUrl();
        return callPost(darajaConfig.getC2bRegisterUrlUrl(), c2bRegister);
    }

    @Override
    public Mono<SyncResponse> c2bSimulate() {
        logger.info("Service c2bSimulate :::");
        C2bSimulate c2BSimulate = getC2bSimulate();
        return callPost(darajaConfig.getC2bSimulateUrl(), c2BSimulate);
    }

    @Override
    public boolean c2bConfirmation(C2bConfirmation c2bConfirmation) {
        logger.info("c2bConfirmation::save payment to DB: {}", c2bConfirmation);
        return true;
    }

    @Override
    public boolean c2bValidation(C2bConfirmation c2bValidation) {
        logger.info("c2bValidation::save payment to DB: {}", c2bValidation);
        return true;
    }

    @Override
    public Mono<SyncResponse> queryTransaction() {
        logger.info("Service queryTransaction :::: ");
        return callPost(darajaConfig.getQueryTransactionUrl(), getTransactionStatusRequest());
    }

    @Override
    public boolean queryTransactionQueueTimeout(TransactionStatusResult statusResponse) {
        logger.info("Service queryTransactionQueueTimeout - TransactionStatusResult statusResponse ::: {}", statusResponse);
        return false;
    }

    @Override
    public boolean queryTransactionResult(TransactionStatusResult statusResponse) {
        logger.info("Service queryTransactionResult - TransactionStatusResult statusResponse ::: {}", statusResponse);
        return false;
    }

    @Override
    public Mono<SyncResponse> initiateQueryBalance() {
        logger.info("Service queryBalance :::: ");
        return callPost(darajaConfig.getQueryBalUrl(), getQueryBalanceRequest());
    }

    @Override
    public boolean queryBalResult(QueryBalanceResult queryBalanceResult) {
        logger.info("Todo: persist - Service queryBalResult ::: {}", queryBalanceResult);

        return false;
    }

    @Override
    public boolean queryBalQueueTimeout(QueryBalanceResult queryBalanceResult) {
        logger.info("Todo: persist - Service queryBalQueueTimeout ::: {}", queryBalanceResult);

        return false;
    }

    @Override
    public Mono<SyncResponse> initiateReversal() {
        logger.info("Service reversal - :::");
        return callPost(darajaConfig.getReversalUrl(), getReversalRequest());
    }

    @Override
    public boolean reversalQueue(ReversalResult reversalResult) {
        logger.info("Todo: persist - Service reversalQueue - ::: {}", reversalResult);

        return false;
    }

    @Override
    public boolean reversalResult(ReversalResult reversalResult) {
        logger.info("Todo: persist - Service reversalResult - ::: {}", reversalResult);

        return false;
    }

    @Override
    public boolean remitTaxResult(RemitTaxResult remitTaxResult) {
        logger.info("Todo: persist - Service remitTaxResult - ::: {}", remitTaxResult);
        return false;
    }

    @Override
    public boolean remitTaxQueue(RemitTaxResult remitTaxResult) {
        logger.info("Todo: persist - Service remitTaxQueue - ::: {}", remitTaxResult);
        return false;
    }

    @Override
    public Mono<SyncResponse> initiateRemitTax() {
        logger.info("Service remitTax - :::");
        return callPost(darajaConfig.getRemitTaxUrl(), getRemitTaxRequest());
    }

    @Override
    public boolean paymentRequestResult(PaymentRequestResult paymentRequestResult) {
        logger.info("Todo: persist - Service paymentRequestResult - ::: {}", paymentRequestResult);
        return false;
    }

    @Override
    public boolean paymentRequestQueue(PaymentRequestResult paymentRequestResult) {
        logger.info("Todo: persist - Service paymentRequestQueue - ::: {}", paymentRequestResult);
        return false;
    }

    @Override
    public Mono<SyncResponse> initiatePaymentRequest() {
        logger.info("Service paymentRequestCall - :::");
        return callPost(darajaConfig.getPaymentRequestUrl(), getPaymentRequestRequest());
    }

    private Mono<SyncResponse> callPost(String url, Object request) {
        logger.info("Service callPost:::::::=>{}", HelperUtil.toJson(request));
        return getValidAccessToken()
                .flatMap(token ->
                        webClient.post()
                                .uri(url)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .bodyValue(request)
                                .retrieve()
                                .onStatus(HttpStatusCode::isError, clientResponse -> {
                                    return clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                                        logger.error("callPost Error Response: {}", errorBody);
                                        return Mono.error(new RuntimeException("callPost failed with status: " + clientResponse.statusCode()));
                                    });
                                })
                                .bodyToMono(SyncResponse.class)
                                .doOnNext(response -> logger.info("callPost Success Response: {}", response))
                                .doOnError(error -> logger.error("callPost Exception: {}", error.getMessage(), error))
                );
    }

    private PaymentRequestRequest getPaymentRequestRequest() {
        PaymentRequestRequest paymentRequestRequest = new PaymentRequestRequest();
        paymentRequestRequest.setInitiator(darajaConfig.getB2cInitiatorName());
        paymentRequestRequest.setSecurityCredential(securityCredential);
        paymentRequestRequest.setCommandID("BusinessPayBill");
        paymentRequestRequest.setSenderIdentifierType("4");
        paymentRequestRequest.setReceiverIdentifierType("4");
        paymentRequestRequest.setAmount("1");
        paymentRequestRequest.setPartyA("600983");
        paymentRequestRequest.setPartyB("600000");
        paymentRequestRequest.setAccountReference("353353");
        paymentRequestRequest.setRequester("254708374149");
        paymentRequestRequest.setRemarks("sample-remarks");
        paymentRequestRequest.setQueueTimeOutURL(darajaConfig.getFullPaymentRequestQueueUrl());
        paymentRequestRequest.setResultURL(darajaConfig.getFullPaymentRequestResultUrl());
        return paymentRequestRequest;
    }

    private B2cRequest getB2cRequest(B2cRequestExternal b2CRequestExternal) {
        B2cRequest b2CRequest = new B2cRequest();
        logger.info("securityCredential encryptPassword :::: {}", securityCredential);
        b2CRequest.setSecurityCredential(securityCredential);
        b2CRequest.setInitiatorName(darajaConfig.getB2cInitiatorName());
        b2CRequest.setOriginatorConversationID(HelperUtil.generate());
        b2CRequest.setQueueTimeOutURL(darajaConfig.getFullB2cCallbackUrl());
        b2CRequest.setResultURL(darajaConfig.getFullB2cResultUrl());
        b2CRequest.setPartyA(darajaConfig.getB2cPartyA());
        /*Add B2cRequestExternal to B2CRequest*/
        b2CRequest.setAmount(b2CRequestExternal.getAmount());
        b2CRequest.setCommandID(b2CRequestExternal.getCommandID());
        b2CRequest.setPartyB(b2CRequestExternal.getPartyB());
        b2CRequest.setRemarks(b2CRequestExternal.getRemarks());
        b2CRequest.setOccassion(b2CRequestExternal.getOccassion());
        logger.info("B2CRequest just before submit :: {} ", HelperUtil.toJson(b2CRequest));
        return b2CRequest;
    }

    private C2bRegisterUrl getC2bRegisterUrl() {
        C2bRegisterUrl c2bRegister = new C2bRegisterUrl();
        c2bRegister.setConfirmationURL(darajaConfig.getFullC2bConfirmationUrl());
        c2bRegister.setValidationURL(darajaConfig.getFullC2bValidationUrl());
        c2bRegister.setResponseType("Completed");
        c2bRegister.setShortCode("107031");
        return c2bRegister;
    }

    private RemitTaxRequest getRemitTaxRequest() {
        RemitTaxRequest remitTaxRequest = new RemitTaxRequest();
        remitTaxRequest.setInitiator(darajaConfig.getB2cInitiatorName());
        remitTaxRequest.setSecurityCredential(securityCredential);
        remitTaxRequest.setCommandID("PayTaxToKRA");
        remitTaxRequest.setSenderIdentifierType("4");
        remitTaxRequest.setReceiverIdentifierType("4");
        remitTaxRequest.setAmount("10");
        remitTaxRequest.setPartyA("600980");
        remitTaxRequest.setPartyB("572572");
        remitTaxRequest.setAccountReference("353353");
        remitTaxRequest.setRemarks("sample-remarks");
        remitTaxRequest.setQueueTimeOutURL(darajaConfig.getFullRemitTaxQueueUrl());
        remitTaxRequest.setResultURL(darajaConfig.getFullRemitTaxResultUrl());
        return remitTaxRequest;
    }

    private C2bSimulate getC2bSimulate() {
        C2bSimulate c2BSimulate = new C2bSimulate();
        c2BSimulate.setCommandID("CustomerPayBillOnline");
        c2BSimulate.setMsisdn("254708374149");
        c2BSimulate.setAmount("10");
        c2BSimulate.setBillRefNumber("paying");
        c2BSimulate.setShortCode("107031");
        return c2BSimulate;
    }

    private TransactionStatusRequest getTransactionStatusRequest() {
        TransactionStatusRequest txnStatusRequest = new TransactionStatusRequest();
        txnStatusRequest.setInitiator(darajaConfig.getB2cInitiatorName());
        txnStatusRequest.setSecurityCredential(securityCredential);
        txnStatusRequest.setCommandID("TransactionStatusQuery");
        txnStatusRequest.setTransactionID("OEI2AK4Q16");
        txnStatusRequest.setOriginatorConversationID("AG_20250425_20101f0bf59d8c36456a");
        txnStatusRequest.setPartyA("600987");
        txnStatusRequest.setIdentifierType("2");
        txnStatusRequest.setResultURL(darajaConfig.getFullQueryTransactionResult());
        txnStatusRequest.setQueueTimeOutURL(darajaConfig.getFullQueryTransactionQueueUrl());
        txnStatusRequest.setRemarks("sample remarks");
        txnStatusRequest.setOccasion(null);
        return txnStatusRequest;
    }

    private ReversalRequest getReversalRequest() {
        ReversalRequest reversalRequest = new ReversalRequest();
        reversalRequest.setInitiator(darajaConfig.getB2cInitiatorName());
        reversalRequest.setSecurityCredential(securityCredential);
        reversalRequest.setCommandID("TransactionReversal");
        reversalRequest.setTransactionID("OEI2AK4Q16");
        reversalRequest.setAmount("1");
        reversalRequest.setReceiverParty("600984");
        reversalRequest.setRecieverIdentifierType("11");
        reversalRequest.setResultURL(darajaConfig.getFullReversalResultUrl());
        reversalRequest.setQueueTimeOutURL(darajaConfig.getFullReversalQueueUrl());
        reversalRequest.setRemarks("test-test");
        reversalRequest.setOccasion("christmas");
        return reversalRequest;
    }

    private QueryBalanceRequest getQueryBalanceRequest() {
        QueryBalanceRequest queryBalanceRequest = new QueryBalanceRequest();
        queryBalanceRequest.setInitiator(darajaConfig.getB2cInitiatorName());
        queryBalanceRequest.setSecurityCredential(securityCredential);
        queryBalanceRequest.setCommandID("AccountBalance");
        queryBalanceRequest.setPartyA("600978");
        queryBalanceRequest.setIdentifierType("2");
        queryBalanceRequest.setRemarks("getbal");
        queryBalanceRequest.setQueueTimeOutURL(darajaConfig.getFullQueryBalQueueUrl());
        queryBalanceRequest.setResultURL(darajaConfig.getFullQueryBalResultUrl());
        return queryBalanceRequest;
    }

    public void printConfig() {
        System.out.println("consumer-key: " + darajaConfig.getConsumerKey());
        System.out.println("consumer-secret: " + darajaConfig.getConsumerSecret());
        System.out.println("security-credential: " + darajaConfig.getSecurityCredential());
        System.out.println("auth-url: " + darajaConfig.getAuthUrl());
        System.out.println("b2c-url: " + darajaConfig.getB2cUrl());
        System.out.println("base-url: " + darajaConfig.getBaseUrl());
        System.out.println("callback-url: " + darajaConfig.getFullB2cCallbackUrl());
        System.out.println("initiate-b2c-url: " + darajaConfig.getFullB2cInitiateUrl());
        System.out.println("b2c-party-a: " + darajaConfig.getB2cPartyA());
        System.out.println("b2c-initiator-name: " + darajaConfig.getB2cInitiatorName());
        System.out.println("b2c-initiator-password: " + darajaConfig.getB2cInitiatorPassword());
        System.out.println("c2b-short-code: " + darajaConfig.getC2bShortCode());
        System.out.println("c2b-register-url: " + darajaConfig.getC2bRegisterUrlUrl());
        System.out.println("c2b-register-call-url: " + darajaConfig.getFullC2bRegisterCallUrl());
        System.out.println("c2b-confirmation-url: " + darajaConfig.getFullC2bConfirmationUrl());
        System.out.println("c2b-validation-url: " + darajaConfig.getFullC2bValidationUrl());
        System.out.println("c2b-simulation-url: " + darajaConfig.getC2bSimulateUrl());
        System.out.println("query-balance-url: " + darajaConfig.getQueryBalUrl());
    }

}
