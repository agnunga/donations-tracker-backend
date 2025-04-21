package io.omosh.dts.services.daraja;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.omosh.dts.config.DarajaConfig;
import io.omosh.dts.dtos.daraja.AccessTokenResponse;
import io.omosh.dts.dtos.daraja.B2CRequest;
import io.omosh.dts.dtos.daraja.SyncResponse;
import io.omosh.dts.utils.HelperUtil;
import jakarta.persistence.Access;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class DarajaApiServiceImpl implements DarajaApiService {

    private final DarajaConfig darajaConfig;
    private final WebClient webClient;
    ObjectMapper objectMapper;
    private String authHeader = "";
    Logger logger = LoggerFactory.getLogger(DarajaApiServiceImpl.class);

    @Autowired
    public DarajaApiServiceImpl(DarajaConfig darajaConfig, WebClient.Builder webClientBuilder) {
        this.darajaConfig = darajaConfig;
        this.webClient = webClientBuilder.build();
        objectMapper = new ObjectMapper();
    }

    @Override
    public Mono<AccessTokenResponse> getAccessToken() {
        try {
            String authHeader = HelperUtil.toBase64(
                    darajaConfig.getConsumerKey() + ":" + darajaConfig.getConsumerSecret()
            );

            logger.info("The auth header :: {}", authHeader);
            logger.info("The auth URL :: {}", darajaConfig.getAuthUrl());

            return webClient.get()
                    .uri(darajaConfig.getAuthUrl())
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + authHeader)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(AccessTokenResponse.class)
                    .doOnError(e -> logger.error("Failed to fetch access token", e));

        } catch (Exception e) {
            // Wrap checked exception (e.g., Base64 encoding) in a Mono error
            return Mono.error(new RuntimeException("Error creating auth header", e));
        }
    }

    @Override
    public Mono<SyncResponse> performB2CTransaction(B2CRequest b2CRequest) {
        return getAccessToken()
                .flatMap(accessTokenResponse -> {
                    // Save the token (locally inside the lambda)
                    try {
                        String tokenJson = objectMapper.writeValueAsString(accessTokenResponse);
                        logger.info("Access Token (JSON): {}", tokenJson);
                    } catch (Exception e) {
                        logger.error("Error serializing access token", e);
                    }

                    System.out.println("accessTokenResponse.getAccessToken()::::   " + accessTokenResponse.getAccessToken());

                    return Mono.just(new SyncResponse());
                });
    }


    public void printConfig() {
        System.out.println("Consumer Key: " + darajaConfig.getConsumerKey());
        System.out.println("Consumer Secret: " + darajaConfig.getConsumerSecret());
    }
}
