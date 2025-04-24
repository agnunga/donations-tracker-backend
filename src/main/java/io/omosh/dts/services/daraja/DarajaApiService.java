package io.omosh.dts.services.daraja;

import io.omosh.dts.dtos.daraja.*;
import reactor.core.publisher.Mono;

public interface DarajaApiService {

    /*Authorization*/
    Mono<AccessTokenResponse> getAccessToken();

    Mono<SyncResponse> getB2CTransactionResults(B2CResponse b2CResponse);

    Mono<SyncResponse> performB2CTransaction(B2CRequestExternal b2CRequestExternal);

    Mono<SyncResponse> c2bRegisterUrl(C2bRegister c2bRegister);


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
