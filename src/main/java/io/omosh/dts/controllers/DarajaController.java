package io.omosh.dts.controllers;

import io.omosh.dts.dtos.daraja.*;
import io.omosh.dts.services.daraja.DarajaApiService;
import io.omosh.dts.utils.HelperUtil;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/momo")
public class DarajaController {

    Logger logger = LoggerFactory.getLogger(DarajaController.class);

    private final DarajaApiService service;

    public DarajaController(DarajaApiService service) {
        this.service = service;
    }

    @SneakyThrows
    @PostMapping(value = "/b2c-initiate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AcknowledgeResponse> initiateB2cPayment(@RequestBody B2cRequestExternal b2CRequestExternal) {
        logger.info("Just in b2CRequestExternal :::: {}", HelperUtil.toJson(b2CRequestExternal));
        service.performB2CTransaction(b2CRequestExternal).subscribe();
        return ResponseEntity.ok(new AcknowledgeResponse("success"));
    }

    @PostMapping(value = "/b2c-callback", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AcknowledgeResponse> paymentCallback(@RequestBody B2cResponse b2CResponse) {
        logger.info("Just in paymentCallback Body: {}", HelperUtil.toJson(b2CResponse));
        service.getB2CTransactionResults(b2CResponse);
        return ResponseEntity.ok(new AcknowledgeResponse("success"));
    }

    @PostMapping(value = "/b2c-result", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AcknowledgeResponse> paymentResult(@RequestBody B2cResponse b2CResponse) {
        logger.info("Just in paymentResult Body: {}", HelperUtil.toJson(b2CResponse));

        return ResponseEntity.ok(new AcknowledgeResponse("success"));
    }

    @PostMapping(value = "/c2b-register-call", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AcknowledgeResponse> c2bRegisterUrl() {
        logger.info("Just in c2bRegisterUrl ::::");
        service.c2bRegisterUrl().subscribe();
        return ResponseEntity.ok(new AcknowledgeResponse("success"));
    }

    @PostMapping(value = "/c2b-confirmation", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<C2bValidationResponse> c2bConfirmation(@RequestBody C2bConfirmation c2bConfirmation) {
        logger.info("Just in c2bConfirmation :::: {}", HelperUtil.toJson(c2bConfirmation));
        boolean success = service.c2bConfirmation(c2bConfirmation);
        if (success)
            return ResponseEntity.ok(new C2bValidationResponse("0", "Accepted"));
        return ResponseEntity.ok(new C2bValidationResponse("C2B00011", "Rejected"));
    }

    @PostMapping(value = "/c2b-validation", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<C2bValidationResponse> c2bValidation(@RequestBody C2bConfirmation c2bValidation) {
        logger.info("Just in c2bValidation :::: {}", HelperUtil.toJson(c2bValidation));
        boolean success = service.c2bValidation(c2bValidation);
        if (success)
            return ResponseEntity.ok(new C2bValidationResponse("0", "Accepted"));
        return ResponseEntity.ok(new C2bValidationResponse("C2B00011", "Rejected"));
    }

    @PostMapping(value = "/c2b-simulate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AcknowledgeResponse> c2bSimulate() {
        logger.info("Just in c2bSimulate :::: ");
        service.c2bSimulate().subscribe();
        return ResponseEntity.ok(new AcknowledgeResponse("success"));
    }

    @PostMapping(value = "/query-transaction-call", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AcknowledgeResponse> queryTransaction() {
        logger.info("Just in c2bValidation :::: ");
        service.queryTransaction().subscribe();
        return ResponseEntity.ok(new AcknowledgeResponse("ok"));
    }

    @PostMapping(value = "/query-transaction-queue", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AcknowledgeResponse> queryTransactionQueueTimeout(@RequestBody TransactionStatusResult statusResponse) {
        logger.info("Just in queryTransactionQueueTimeout :::: {}", HelperUtil.toJson(statusResponse));
        boolean success = service.queryTransactionQueueTimeout(statusResponse);
        if (success)
            return ResponseEntity.ok(new AcknowledgeResponse("ok"));
        return ResponseEntity.ok(new AcknowledgeResponse("nok"));
    }

    @PostMapping(value = "/query-transaction-result", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AcknowledgeResponse> queryTransactionResult(@RequestBody TransactionStatusResult statusResponse) {
        logger.info("Just in queryTransactionResult :::: {}", HelperUtil.toJson(statusResponse));
        boolean success = service.queryTransactionResult(statusResponse);
        if (success)
            return ResponseEntity.ok(new AcknowledgeResponse("ok"));
        return ResponseEntity.ok(new AcknowledgeResponse("nok"));
    }

    @PostMapping(value = "/query-bal-call", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AcknowledgeResponse> queryBalCall() {
        logger.info("Just in queryBalCall :::: {}", "No req body");
        service.queryBalance().subscribe();
        return ResponseEntity.ok(new AcknowledgeResponse("ok"));
    }

    @PostMapping(value = "/query-bal-queue", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AcknowledgeResponse> queryBalQueueTimeout(@RequestBody QueryBalanceResult queryBalanceResult) {
        logger.info("Just in queryBalQueueTimeout :::: {}", HelperUtil.toJson(queryBalanceResult));
        boolean success = service.queryBalQueueTimeout(queryBalanceResult);
        if (success)
            return ResponseEntity.ok(new AcknowledgeResponse("ok"));
        return ResponseEntity.ok(new AcknowledgeResponse("nok"));
    }

    @PostMapping(value = "/query-bal-result", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AcknowledgeResponse> queryBalResult(@RequestBody QueryBalanceResult queryBalanceResult) {
        logger.info("Just in queryBalResult :::: {}", HelperUtil.toJson(queryBalanceResult));
        boolean success = service.queryBalResult(queryBalanceResult);
        if (success)
            return ResponseEntity.ok(new AcknowledgeResponse("ok"));
        return ResponseEntity.ok(new AcknowledgeResponse("nok"));
    }

    @PostMapping(value = "/reversal-result", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AcknowledgeResponse> reversalResult(@RequestBody ReversalResult reversalResult) {
        logger.info("Just in reversalResult :::: {}", HelperUtil.toJson(reversalResult));
        boolean success = service.reversalResult(reversalResult);
        if (success)
            return ResponseEntity.ok(new AcknowledgeResponse("ok"));
        return ResponseEntity.ok(new AcknowledgeResponse("nok"));
    }

    @PostMapping(value = "/reversal-queue", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AcknowledgeResponse> reversalQueue(@RequestBody ReversalResult reversalResult) {
        logger.info("Just in reversalQueue :::: {}", HelperUtil.toJson(reversalResult));
        boolean success = service.reversalQueue(reversalResult);
        if (success)
            return ResponseEntity.ok(new AcknowledgeResponse("ok"));
        return ResponseEntity.ok(new AcknowledgeResponse("nok"));
    }

    @PostMapping(value = "/reversal-call", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AcknowledgeResponse> reversalCall() {
        logger.info("Just in reversalCall :::: ");
        service.reversal().subscribe();
        return ResponseEntity.ok(new AcknowledgeResponse("ok"));
    }

}
