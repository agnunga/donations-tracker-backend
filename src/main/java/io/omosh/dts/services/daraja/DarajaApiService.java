package io.omosh.dts.services.daraja;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.omosh.dts.dtos.daraja.AccessTokenResponse;
import io.omosh.dts.dtos.daraja.B2CRequest;
import io.omosh.dts.dtos.daraja.SyncResponse;
import reactor.core.publisher.Mono;

public interface DarajaApiService {

    /*Authorization*/
    Mono<AccessTokenResponse> getAccessToken();

    Mono<SyncResponse> performB2CTransaction(B2CRequest b2CRequest);


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
