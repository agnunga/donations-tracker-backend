package io.omosh.dts.services.daraja;

import io.omosh.dts.dtos.daraja.*;
import reactor.core.publisher.Mono;

public interface DarajaApiService {

    /*Authorization*/
    Mono<AccessTokenResponse> getAccessToken();

    Mono<SyncResponse> getB2CTransactionResults(B2cResult b2CResult);

    Mono<SyncResponse> performB2CTransaction(B2cRequestExternal b2CRequestExternal);

    Mono<SyncResponse> c2bRegisterUrl();

    Mono<SyncResponse> c2bSimulate();

    boolean c2bConfirmation(C2bConfirmation c2bConfirmation);

    boolean c2bValidation(C2bConfirmation c2bValidation);

    Mono<SyncResponse> queryTransaction();

    boolean queryTransactionQueueTimeout(TransactionStatusResult statusResponse);

    boolean queryTransactionResult(TransactionStatusResult statusResponse);

    Mono<SyncResponse> initiateQueryBalance();

    boolean queryBalResult(QueryBalanceResult queryBalanceResult);

    boolean queryBalQueueTimeout(QueryBalanceResult queryBalanceResult);

    Mono<SyncResponse> initiateReversal();

    boolean reversalQueue(ReversalResult reversalResult);

    boolean reversalResult(ReversalResult reversalResult);

    boolean remitTaxResult(RemitTaxResult remitTaxResult);

    boolean remitTaxQueue(RemitTaxResult remitTaxResult);

    Mono<SyncResponse> initiateRemitTax();

    boolean paymentRequestResult(PaymentRequestResult paymentRequestResult);

    boolean paymentRequestQueue(PaymentRequestResult paymentRequestResult);

    Mono<SyncResponse> initiatePaymentRequest();

    /*Customer To Business (C2B)*/
    /*
    RegisterUrlResponse registerUrl();

    SimulateTransactionResponse simulateC2BTransaction(SimulateTransactionRequest simulateTransactionRequest);


    TransactionStatusSyncResponse getTransactionResult(InternalTransactionStatusRequest internalTransactionStatusRequest);

    CommonSyncResponse checkAccountBalance();

    StkPushSyncResponse performStkPushTransaction(InternalStkPushRequest internalStkPushRequest);

    LNMQueryResponse getTransactionStatus(InternalLNMRequest internalLNMRequest);

    */
}
