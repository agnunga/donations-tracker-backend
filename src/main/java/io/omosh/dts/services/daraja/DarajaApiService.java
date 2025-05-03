package io.omosh.dts.services.daraja;

import io.omosh.dts.dtos.daraja.*;

import java.util.Optional;

public interface DarajaApiService {

    /*Authorization*/
    Optional<AccessTokenResponse> getAccessToken();

    SyncResponse getB2CTransactionResults(B2cResult b2CResult);

    SyncResponse performB2CTransaction(B2cRequestExternal b2CRequestExternal);

    SyncResponse c2bRegisterUrl();

    SyncResponse c2bSimulate();

    boolean c2bConfirmation(C2bConfirmation c2bConfirmation);

    boolean c2bValidation(C2bConfirmation c2bValidation);

    SyncResponse queryTransaction();

    boolean queryTransactionQueueTimeout(TransactionStatusResult statusResponse);

    boolean queryTransactionResult(TransactionStatusResult statusResponse);

    SyncResponse initiateQueryBalance();

    boolean queryBalResult(QueryBalanceResult queryBalanceResult);

    boolean queryBalQueueTimeout(QueryBalanceResult queryBalanceResult);

    SyncResponse initiateReversal();

    boolean reversalQueue(ReversalResult reversalResult);

    boolean reversalResult(ReversalResult reversalResult);

    boolean remitTaxResult(RemitTaxResult remitTaxResult);

    boolean remitTaxQueue(RemitTaxResult remitTaxResult);

    SyncResponse initiateRemitTax();

    boolean paymentRequestResult(PaymentRequestResult paymentRequestResult);

    boolean paymentRequestQueue(PaymentRequestResult paymentRequestResult);

    SyncResponse initiatePaymentRequest();

    /*M-Pesa Express callback*/
    boolean stkPushCallback(ExpressResult expressResult);

    /*M-Pesa Express Simulate*/
    ExpressResponse initiateStkPushRequest();

    /*M-Pesa Express Query*/
    ExpressQueryResponse initiateStkPushQuery();

    GenerateQrResponse initiateGenerateQR();

    void printConfig();

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
