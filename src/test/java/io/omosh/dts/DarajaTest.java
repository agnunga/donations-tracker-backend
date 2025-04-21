package io.omosh.dts;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.omosh.dts.dtos.daraja.AccessTokenResponse;
import io.omosh.dts.services.daraja.DarajaApiServiceImpl;
import io.omosh.dts.utils.HelperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;
import org.springframework.http.MediaType;


public class DarajaTest {

    private final WebClient webClient;

    public DarajaTest(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://sandbox.safaricom.co.ke").build();
    }

    public Mono<AccessTokenResponse> getAccessToken(String token) {
        darajaApiServiceImpl.printConfig();
        return webClient.get()
                .uri("/oauth/v1/generate?grant_type=client_credentials")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(AccessTokenResponse.class); // or use a custom response class
    }

    public static void printResponse(String secretKey) throws Exception {
        DarajaTest darajaAuthService = new DarajaTest(WebClient.builder());
        Mono<AccessTokenResponse> res = darajaAuthService.getAccessToken(HelperUtil.toBase64(secretKey)); // Only for testing outside reactive context
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(objectMapper.writeValueAsString(res.block()));
    }

    @Autowired
    private DarajaApiServiceImpl darajaApiServiceImpl;

    //testing
    final static String SECRET_KEY = "";
    public static void main(String[] args) {
        try {
            printResponse(SECRET_KEY);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
