package io.omosh.dts.services.daraja;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.omosh.dts.config.DarajaConfig;
import io.omosh.dts.dtos.daraja.*;
import io.omosh.dts.dtos.daraja.enums.ExpressTrxCode;
import io.omosh.dts.dtos.daraja.enums.TransactionType;
import io.omosh.dts.utils.HelperUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class DarajaApiServiceImpl implements DarajaApiService {

    private final DarajaConfig darajaConfig;
    private final OkHttpClient okHttpClient;
    ObjectMapper objectMapper;
    private final Logger logger = LoggerFactory.getLogger(DarajaApiServiceImpl.class);

    private AccessTokenResponse cachedToken;
    private Instant expiryTime;
    private final String securityCredential;

    @Autowired
    public DarajaApiServiceImpl(DarajaConfig darajaConfig) {
        this.darajaConfig = darajaConfig;
        this.okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)  // Adjust connection timeout
                .writeTimeout(30, TimeUnit.SECONDS)    // Adjust write timeout
                .readTimeout(30, TimeUnit.SECONDS)     // Adjust read timeout
                .build();
        objectMapper = new ObjectMapper();
        this.securityCredential = HelperUtil.encryptPasswordWithCert(darajaConfig.getB2cInitiatorPassword());
    }

    public synchronized Optional<String> getValidAccessToken() {
        if (cachedToken != null && Instant.now().isBefore(expiryTime)) {
            return Optional.of(cachedToken.getAccessToken());
        }

        return getAccessToken()
                .map(token -> {
                    this.cachedToken = token;
                    this.expiryTime = Instant.now().plusSeconds(59 * 60 - 60); // 58 min
                    return token.getAccessToken();
                });
    }

    @Override
    public Optional<AccessTokenResponse> getAccessToken() {
        String authHeader = HelperUtil.toBase64(
                darajaConfig.getConsumerKey() + ":" + darajaConfig.getConsumerSecret()
        );

        logger.info("The auth header :: {}", authHeader);
        logger.info("The auth URL :: {}", darajaConfig.getAuthUrl());

        Request request = new Request.Builder()
                .url(darajaConfig.getAuthUrl())
                .get()
                .addHeader(HttpHeaders.AUTHORIZATION, "Basic " + authHeader)
                .addHeader(HttpHeaders.CACHE_CONTROL, "no-cache")
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.body() == null) {
                logger.error("UserDTO body is null while retrieving access token");
                return Optional.empty();
            }

            String responseBody = response.body().string();
            AccessTokenResponse tokenResponse = HelperUtil.fromJson(responseBody, AccessTokenResponse.class);
            return Optional.ofNullable(tokenResponse);
        } catch (IOException e) {
            logger.error("Could not get access token: {}", e.getLocalizedMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public SyncResponse getB2CTransactionResults(B2cResult b2CResult) {
        logger.info("b2CResult ::: {}", HelperUtil.toJson(b2CResult));
        return null;
    }

    @Override
    public SyncResponse performB2CTransaction(B2cRequestExternal b2CRequestExternal) {
        logger.info("Service performB2CTransaction :::");
        B2cRequest b2CRequest = getB2cRequest(b2CRequestExternal);
        return callPost(darajaConfig.getB2cUrl(), b2CRequest, SyncResponse.class);
    }

    @Override
    public SyncResponse c2bRegisterUrl() {
        logger.info("Service c2bRegisterUrl :::");
        C2bRegisterUrl c2bRegister = getC2bRegisterUrl();
        return callPost(darajaConfig.getC2bRegisterUrlUrl(), c2bRegister, SyncResponse.class);
    }

    @Override
    public SyncResponse c2bSimulate() {
        logger.info("Service c2bSimulate :::");
        C2bSimulate c2BSimulate = getC2bSimulate();
        return callPost(darajaConfig.getC2bSimulateUrl(), c2BSimulate, SyncResponse.class);
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
    public SyncResponse queryTransaction() {
        logger.info("Service queryTransaction :::: ");
        return callPost(darajaConfig.getQueryTransactionUrl(), getTransactionStatusRequest(), SyncResponse.class);
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
    public SyncResponse initiateQueryBalance() {
        logger.info("Service queryBalance :::: ");
        return callPost(darajaConfig.getQueryBalUrl(), getQueryBalanceRequest(), SyncResponse.class);
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
    public SyncResponse initiateReversal() {
        logger.info("Service reversal - :::");
        return callPost(darajaConfig.getReversalUrl(), getReversalRequest(), SyncResponse.class);
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
    public SyncResponse initiateRemitTax() {
        logger.info("Service remitTax - :::");
        return callPost(darajaConfig.getRemitTaxUrl(), getRemitTaxRequest(), SyncResponse.class);
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

    /*Business Pay Bill - payment request*/
    @Override
    public SyncResponse initiatePaymentRequest() {
        logger.info("Service paymentRequestCall - :::");
        return callPost(darajaConfig.getPaymentRequestUrl(), getPaymentRequestRequest(), SyncResponse.class);
    }

    /*M-Pesa Express callback*/
    @Override
    public boolean stkPushCallback(ExpressResult expressResult) {
        logger.info("Todo: persist - Service stkPushCallback - ::: {}", expressResult);
        return false;
    }

    /*M-Pesa Express Simulate*/
    @Override
    public ExpressResponse initiateStkPushRequest() {
        logger.info("Service initiateStkPushRequest - :::");
        return callPost(darajaConfig.getExpressUrl(), getExpressRequest(), ExpressResponse.class);
    }

    @Override
    public ExpressQueryResponse initiateStkPushQuery() {
        logger.info("Service initiateStkPushQuery - :::");
        return callPost(darajaConfig.getExpressQueryUrl(), getExpressQueryRequest(), ExpressQueryResponse.class);
    }

    @Override
    public GenerateQrResponse initiateGenerateQR() {
        logger.info("Service initiateGenerateQR - :::");
        return callPost(darajaConfig.getGenerateQrUrl(), getGenerateQrRequest(), GenerateQrResponse.class);
    }

    private <T> T callPost(String url, Object request, Class<T> responseType) {
        logger.info("Service callPost:::::::=> {}", HelperUtil.toJson(request));

        String token = getValidAccessToken()
                .orElseThrow(() -> new RuntimeException("Unable to retrieve access token"));

        if (token.isBlank()) {
            throw new RuntimeException("Access token is blank");
        }
        try {
            OkHttpClient client = new OkHttpClient();

            RequestBody body = RequestBody.create(Constants.JSON_MEDIA_TYPE,
                    Objects.requireNonNull(HelperUtil.toJson(request)));

            Request httpRequest = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + token)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = client.newCall(httpRequest).execute()) {
                String responseBody = response.body().string();

                if (!response.isSuccessful()) {
                    logger.error("callPost Error UserDTO: {}", responseBody);
                    throw new RuntimeException("Error from service: " + responseBody);
                }

                logger.info("callPost Success UserDTO: {}", responseBody);
                return objectMapper.readValue(responseBody, responseType);
            }

        } catch (IOException e) {
            logger.error("callPost Exception: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to execute POST request", e);
        }
    }


    private static ExpressQueryRequest getExpressQueryRequest() {
        ExpressQueryRequest expressQueryRequest = new ExpressQueryRequest();
        String timestamp = HelperUtil.formattedCurrentDateTime();
        String password = HelperUtil.toBase64("174379" + "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919" + timestamp);
        expressQueryRequest.setBusinessShortCode("174379");
        expressQueryRequest.setPassword(password);
        expressQueryRequest.setTimestamp(timestamp);
        expressQueryRequest.setCheckoutRequestID("ws_CO_260520211133524545");
        return expressQueryRequest;
    }

    private static GenerateQrRequest getGenerateQrRequest() {
        GenerateQrRequest generateQrRequest = new GenerateQrRequest();
        generateQrRequest.setMerchantName("TEST-Supermarket");
        generateQrRequest.setRefNo("Invoice Test");
        generateQrRequest.setAmount("1");
        generateQrRequest.setTrxCode(ExpressTrxCode.BG.name());
        generateQrRequest.setCPI("174379");
        generateQrRequest.setSize("300");
        return generateQrRequest;
    }

    private ExpressRequest getExpressRequest() {
        String timestamp = HelperUtil.formattedCurrentDateTime();
        String password = HelperUtil.toBase64("174379" + "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919" + timestamp);
        ExpressRequest expressRequest = new ExpressRequest();
        expressRequest.setBusinessShortCode("174379");
        expressRequest.setPassword(password);
        expressRequest.setTimestamp(timestamp);
        expressRequest.setTransactionType(TransactionType.CustomerPayBillOnline.name());
        expressRequest.setAmount("1");
        expressRequest.setPartyA("254708374149");
        expressRequest.setPartyB("174379");
        expressRequest.setPhoneNumber("254708374149");
        expressRequest.setCallBackURL(darajaConfig.getExpressResultUrl());
        expressRequest.setAccountReference("test");
        expressRequest.setTransactionDesc("test");
        return expressRequest;
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
        paymentRequestRequest.setQueueTimeOutURL(darajaConfig.getPaymentRequestQueueUrl());
        paymentRequestRequest.setResultURL(darajaConfig.getPaymentRequestResultUrl());
        return paymentRequestRequest;
    }

    private B2cRequest getB2cRequest(B2cRequestExternal b2CRequestExternal) {
        B2cRequest b2CRequest = new B2cRequest();
        logger.info("securityCredential encryptPassword :::: {}", securityCredential);
        b2CRequest.setSecurityCredential(securityCredential);
        b2CRequest.setInitiatorName(darajaConfig.getB2cInitiatorName());
        b2CRequest.setOriginatorConversationID(HelperUtil.generate());
        b2CRequest.setQueueTimeOutURL(darajaConfig.getB2cCallbackUrl());
        b2CRequest.setResultURL(darajaConfig.getB2cResultUrl());
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
        c2bRegister.setConfirmationURL(darajaConfig.getC2bConfirmationUrl());
        c2bRegister.setValidationURL(darajaConfig.getC2bValidationUrl());
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
        remitTaxRequest.setQueueTimeOutURL(darajaConfig.getRemitTaxQueueUrl());
        remitTaxRequest.setResultURL(darajaConfig.getRemitTaxResultUrl());
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
        txnStatusRequest.setResultURL(darajaConfig.getQueryTransactionResult());
        txnStatusRequest.setQueueTimeOutURL(darajaConfig.getQueryTransactionQueueUrl());
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
        reversalRequest.setResultURL(darajaConfig.getReversalResultUrl());
        reversalRequest.setQueueTimeOutURL(darajaConfig.getReversalQueueUrl());
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
        queryBalanceRequest.setRemarks("get-bal");
        queryBalanceRequest.setQueueTimeOutURL(darajaConfig.getQueryBalQueueUrl());
        queryBalanceRequest.setResultURL(darajaConfig.getQueryBalResultUrl());
        return queryBalanceRequest;
    }

    public void printConfig() {
        logger.info("Daraja Config variables ::: {}", HelperUtil.toJson(darajaConfig));
    }

}
