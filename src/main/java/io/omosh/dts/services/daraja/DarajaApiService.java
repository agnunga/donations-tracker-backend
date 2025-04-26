package io.omosh.dts.services.daraja;

import io.omosh.dts.dtos.daraja.*;
import reactor.core.publisher.Mono;

public interface DarajaApiService {

    /*Authorization*/
    Mono<AccessTokenResponse> getAccessToken();

    Mono<SyncResponse> getB2CTransactionResults(B2cResponse b2CResponse);

    Mono<SyncResponse> performB2CTransaction(B2cRequestExternal b2CRequestExternal);

    Mono<SyncResponse> c2bRegisterUrl();

    Mono<SyncResponse> c2bSimulate();

    boolean c2bConfirmation(C2bConfirmation c2bConfirmation);

    boolean c2bValidation(C2bConfirmation c2bValidation);

    Mono<SyncResponse> queryTransaction();

    boolean queryTransactionQueueTimeout(TransactionStatusResult statusResponse);

    boolean queryTransactionResult(TransactionStatusResult statusResponse);

    Mono<SyncResponse> queryBalance();

    boolean queryBalResult(QueryBalanceResult queryBalanceResult);

    boolean queryBalQueueTimeout(QueryBalanceResult queryBalanceResult);

    Mono<SyncResponse> reversal();

    boolean reversalQueue(ReversalResult reversalResult);

    boolean reversalResult(ReversalResult reversalResult);


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
