package io.omosh.dts.services.daraja;

import io.omosh.dts.dtos.daraja.*;
import reactor.core.publisher.Mono;

public interface DarajaApiService {

    /*Authorization*/
    Mono<AccessTokenResponse> getAccessToken();

    Mono<SyncResponse> getB2CTransactionResults(B2CResponse b2CResponse);

    Mono<SyncResponse> performB2CTransaction(B2CRequestExternal b2CRequestExternal);

    Mono<SyncResponse> c2bRegisterUrl();

    Mono<SyncResponse> c2bSimulate();

    boolean c2bConfirmation(C2bConfirmation c2bConfirmation);

    boolean c2bValidation(C2bConfirmation c2bValidation);

    Mono<SyncResponse> queryTransaction();

    boolean queryTransactionQueueTimeout(TransactionStatusResponse statusResponse);

    boolean queryTransactionResult(TransactionStatusResponse statusResponse);


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
