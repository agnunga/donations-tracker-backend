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
    public ResponseEntity<AcknowledgeResponse> initiateB2cPayment(@RequestBody B2CRequestExternal b2CRequestExternal) {
        logger.info("Just in b2CRequestExternal :::: {}", HelperUtil.toJson(b2CRequestExternal));
        service.performB2CTransaction(b2CRequestExternal).subscribe();
        return ResponseEntity.ok(new AcknowledgeResponse("success"));
    }

    @PostMapping(value = "/b2c-callback", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AcknowledgeResponse> paymentCallback(@RequestBody B2CResponse b2CResponse) {
        logger.info("Just in paymentCallback Body: {}", HelperUtil.toJson(b2CResponse));
        service.getB2CTransactionResults(b2CResponse);
        return ResponseEntity.ok(new AcknowledgeResponse("success"));
    }

    @PostMapping(value = "/b2c-result", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AcknowledgeResponse> paymentResult(@RequestBody B2CResponse b2CResponse) {
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

    @PostMapping(value = "/query-transaction-queue-timeout", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AcknowledgeResponse> queryTransactionQueueTimeout(@RequestBody TransactionStatusResponse statusResponse) {
        logger.info("Just in queryTransactionQueueTimeout :::: {}", HelperUtil.toJson(statusResponse));
        boolean success = service.queryTransactionQueueTimeout(statusResponse);
        if (success)
            return ResponseEntity.ok(new AcknowledgeResponse("ok"));
        return ResponseEntity.ok(new AcknowledgeResponse("nok"));
    }

    @PostMapping(value = "/query-transaction-result", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AcknowledgeResponse> queryTransactionResult(@RequestBody TransactionStatusResponse statusResponse) {
        logger.info("Just in queryTransactionResult :::: {}", HelperUtil.toJson(statusResponse));
        boolean success = service.queryTransactionResult(statusResponse);
        if (success)
            return ResponseEntity.ok(new AcknowledgeResponse("ok"));
        return ResponseEntity.ok(new AcknowledgeResponse("nok"));
    }

}
