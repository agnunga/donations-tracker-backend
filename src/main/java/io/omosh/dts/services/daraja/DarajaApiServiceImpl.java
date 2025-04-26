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
    private String securityCredential;

    @Autowired
    public DarajaApiServiceImpl(DarajaConfig darajaConfig, WebClient.Builder webClientBuilder) {
        this.darajaConfig = darajaConfig;
        this.webClient = webClientBuilder.build();
        objectMapper = new ObjectMapper();
        this.securityCredential = securityCredential();
    }

    private String securityCredential() {
        return HelperUtil.encryptPassword(darajaConfig.getB2cInitiatorPassword());
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
    public Mono<SyncResponse> getB2CTransactionResults(B2CResponse b2CResponse) {
        logger.info("b2CResponse ::: {}", HelperUtil.toJson(b2CResponse));
        return null;
    }

    @Override
    public Mono<SyncResponse> performB2CTransaction(B2CRequestExternal b2CRequestExternal) {
        B2cRequest b2CRequest = new B2cRequest();
        logger.info("securityCredential encryptPassword :::: {}", securityCredential);
        b2CRequest.setSecurityCredential(securityCredential);
        b2CRequest.setInitiatorName(darajaConfig.getB2cInitiatorName());
        b2CRequest.setOriginatorConversationID(HelperUtil.generate());
        b2CRequest.setQueueTimeOutURL(darajaConfig.getFullB2cCallbackUrl());
        b2CRequest.setResultURL(darajaConfig.getFullB2cResultUrl());
        b2CRequest.setPartyA(darajaConfig.getB2cPartyA());
        /*Add B2CRequestExternal to B2CRequest*/
        b2CRequest.setAmount(b2CRequestExternal.getAmount());
        b2CRequest.setCommandID(b2CRequestExternal.getCommandID());
        b2CRequest.setPartyB(b2CRequestExternal.getPartyB());
        b2CRequest.setRemarks(b2CRequestExternal.getRemarks());
        b2CRequest.setOccassion(b2CRequestExternal.getOccassion());
        logger.info("B2CRequest just before submit :: {} ", HelperUtil.toJson(b2CRequest));

        return getValidAccessToken()
                .flatMap(token ->
                        webClient.post()
                                .uri(darajaConfig.getB2cUrl())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(b2CRequest)
                                .retrieve()
                                .bodyToMono(SyncResponse.class)
                                .doOnNext(response -> logger.info("Access token: Received response: {}", response))
                                .doOnError(error -> logger.error("POST request failed", error))
                );
    }

    @Override
    public Mono<SyncResponse> c2bRegisterUrl() {
        C2bRegister c2bRegister = new C2bRegister();
        c2bRegister.setConfirmationURL(darajaConfig.getFullC2bConfirmationUrl());
        c2bRegister.setValidationURL(darajaConfig.getFullC2bValidationUrl());
        c2bRegister.setResponseType("Completed");
        c2bRegister.setShortCode("107031");

        logger.info("c2bRegister ::: {}", HelperUtil.toJson(c2bRegister));
        return getValidAccessToken()
                .flatMap(token ->
                        webClient.post()
                                .uri(darajaConfig.getC2bRegisterUrl())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(c2bRegister)
                                .retrieve()
                                .bodyToMono(SyncResponse.class)
                                .doOnNext(response -> logger.info("C2B Reg URL: Received response: {}", response))
                                .doOnError(error -> logger.error("POST request failed", error))
                );
    }

    @Override
    public Mono<SyncResponse> c2bSimulate() {
        String simulationUrl = darajaConfig.getC2bSimulateUrl();
        C2bSumulate c2bSumulate = new C2bSumulate();
        c2bSumulate.setCommandID("CustomerPayBillOnline");
        c2bSumulate.setMsisdn("254708374149");
        c2bSumulate.setAmount("10");
        c2bSumulate.setBillRefNumber("paying");
        c2bSumulate.setShortCode("107031");

        logger.info("c2bSimulate :::: {}", c2bSumulate);
        logger.info("simulationUrl :::: {}", simulationUrl);

        return getValidAccessToken()
                .flatMap(token ->
                        webClient.post()
                                .uri(simulationUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .bodyValue(c2bSumulate)
                                .retrieve()
                                .onStatus(HttpStatusCode::isError, clientResponse -> {
                                    return clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                                        logger.error("C2B Simulation Error Response: {}", errorBody);
                                        return Mono.error(new RuntimeException("C2B Simulation failed with status: " + clientResponse.statusCode()));
                                    });
                                })
                                .bodyToMono(SyncResponse.class)
                                .doOnNext(response -> logger.info("C2B Simulation Success Response: {}", response))
                                .doOnError(error -> logger.error("C2B Simulation Exception: {}", error.getMessage(), error))
                );
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
        String url = darajaConfig.getQueryTransactionUrl();
        TransactionStatusRequest txnStatusRequest = getTransactionStatusRequest();
        logger.info("queryTransaction -> txnStatusRequest :::: {}", txnStatusRequest);

        return getValidAccessToken()
                .flatMap(token ->
                        webClient.post()
                                .uri(url)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .bodyValue(txnStatusRequest)
                                .retrieve()
                                .onStatus(HttpStatusCode::isError, clientResponse -> {
                                    return clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                                        logger.error("Query txn Error Response: {}", errorBody);
                                        return Mono.error(new RuntimeException("Query txn failed with status: " + clientResponse.statusCode()));
                                    });
                                })
                                .bodyToMono(SyncResponse.class)
                                .doOnNext(response -> logger.info("query txn Success Response: {}", response))
                                .doOnError(error -> logger.error("query txn Exception: {}", error.getMessage(), error))
                );

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
        txnStatusRequest.setQueueTimeOutURL(darajaConfig.getFullQueryTransactionQueueTimeoutUrl());
        txnStatusRequest.setRemarks("sample remarks");
        txnStatusRequest.setOccasion(null);
        return txnStatusRequest;
    }

    @Override
    public boolean queryTransactionQueueTimeout(TransactionStatusResponse statusResponse) {
        logger.info("Service queryTransactionQueueTimeout - TransactionStatusResponse statusResponse ::: {}", statusResponse);
        return false;
    }

    @Override
    public boolean queryTransactionResult(TransactionStatusResponse statusResponse) {
        logger.info("Service queryTransactionResult - TransactionStatusResponse statusResponse ::: {}", statusResponse);
        return false;
    }

    @Override
    public Mono<SyncResponse> queryBalance() {
        logger.info("Service queryBalance");
        String url = darajaConfig.getQueryBalanceUrl();
        QueryBalanceRequest queryBalanceRequest = getQueryBalanceRequest();

        logger.info("queryBalanceRequest ::::::: {}", HelperUtil.toJson(queryBalanceRequest));

        return getValidAccessToken()
                .flatMap(token ->
                        webClient.post()
                                .uri(url)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .bodyValue(queryBalanceRequest)
                                .retrieve()
                                .onStatus(HttpStatusCode::isError, clientResponse -> {
                                    return clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                                        logger.error("Query bal Error Response: {}", errorBody);
                                        return Mono.error(new RuntimeException("Query bal failed with status: " + clientResponse.statusCode()));
                                    });
                                })
                                .bodyToMono(SyncResponse.class)
                                .doOnNext(response -> logger.info("query bal Success Response: {}", response))
                                .doOnError(error -> logger.error("query bal Exception: {}", error.getMessage(), error))
                );

    }

    private QueryBalanceRequest getQueryBalanceRequest() {
        QueryBalanceRequest queryBalanceRequest = new QueryBalanceRequest();
        queryBalanceRequest.setInitiator(darajaConfig.getB2cInitiatorName());
        queryBalanceRequest.setSecurityCredential(securityCredential);
        queryBalanceRequest.setCommandID("AccountBalance");
        queryBalanceRequest.setPartyA("600978");
        queryBalanceRequest.setIdentifierType("2");
        queryBalanceRequest.setRemarks("getbal");
        queryBalanceRequest.setQueueTimeOutURL(darajaConfig.getFullQueryBalQueueTimeoutURL());
        queryBalanceRequest.setResultURL(darajaConfig.getFullQueryBalResultURL());
        return queryBalanceRequest;
    }

    @Override
    public boolean queryBalResult(QueryBalanceResult queryBalanceResult) {
        logger.info("Todo: persist - Service queryBalResult - queryBalanceResult ::: {}", queryBalanceResult);

        return false;
    }

    @Override
    public boolean queryBalQueueTimeout(QueryBalanceResult queryBalanceResult) {
        logger.info("Todo: persist - Service queryBalQueueTimeout - queryBalanceResult ::: {}", queryBalanceResult);

        return false;
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
        System.out.println("c2b-register-url: " + darajaConfig.getC2bRegisterUrl());
        System.out.println("c2b-register-call-url: " + darajaConfig.getFullC2bRegisterCallUrl());
        System.out.println("c2b-confirmation-url: " + darajaConfig.getFullC2bConfirmationUrl());
        System.out.println("c2b-validation-url: " + darajaConfig.getFullC2bValidationUrl());
        System.out.println("c2b-simulation-url: " + darajaConfig.getC2bSimulateUrl());
        System.out.println("query-balance-url: " + darajaConfig.getQueryBalanceUrl());
    }

}
