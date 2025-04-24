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

    @PostMapping(value = "/c2b-register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AcknowledgeResponse> c2bRegisterUrl(@RequestBody C2bRegister c2bRegister) {
        logger.info("Just in c2bRegisterUrl :::: {}", HelperUtil.toJson(c2bRegister));
        service.c2bRegisterUrl(c2bRegister).subscribe();
        return ResponseEntity.ok(new AcknowledgeResponse("success"));
    }

    @PostMapping(value = "/c2b-confirmation", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AcknowledgeResponse> c2bConfirmation(@RequestBody C2bConfirmation c2bConfirmation) {
        logger.info("Just in c2bConfirmation :::: {}", HelperUtil.toJson(c2bConfirmation));
        boolean success = service.c2bConfirmation(c2bConfirmation);
        return ResponseEntity.ok(new AcknowledgeResponse("success"));
    }

    @PostMapping(value = "/c2b-validation", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AcknowledgeResponse> c2bValidation(@RequestBody C2bConfirmation c2bValidation) {
        logger.info("Just in c2bValidation :::: {}", HelperUtil.toJson(c2bValidation));
        boolean success = service.c2bValidation(c2bValidation);
        if (success)
            return ResponseEntity.ok(new AcknowledgeResponse("success"));
        return ResponseEntity.ok(new AcknowledgeResponse("fail"));
    }

    @PostMapping(value = "/c2b-simulate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AcknowledgeResponse> c2bSimulate(@RequestBody Object object) {
        logger.info("Just in c2bSimulate :::: {}", HelperUtil.toJson(object));
        service.c2bSimulate().subscribe();
        return ResponseEntity.ok(new AcknowledgeResponse("success"));
    }

}
