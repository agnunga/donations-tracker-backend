package io.omosh.dts.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.omosh.dts.dtos.daraja.AcknowledgeResponse;
import io.omosh.dts.dtos.daraja.B2CRequest;
import io.omosh.dts.dtos.daraja.B2CResponse;
import io.omosh.dts.services.daraja.DarajaApiService;
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

    @PostMapping(value = "/b2c-callback", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AcknowledgeResponse> paymentResult(@RequestBody B2CResponse serviceResponse) throws Exception {
        System.out.println("Entering paymentResult method");
        ObjectMapper objectMapper = new ObjectMapper();

        service.performB2CTransaction(new B2CRequest()).subscribe();

        try {
            String jsonString = objectMapper.writeValueAsString(serviceResponse);
            logger.info("Request Body: {}", jsonString);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(new AcknowledgeResponse("success"));
    }

}
