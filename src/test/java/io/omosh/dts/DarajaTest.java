package io.omosh.dts;

import io.omosh.dts.dtos.daraja.AccessTokenResponse;
import io.omosh.dts.services.daraja.DarajaApiService;
import io.omosh.dts.utils.HelperUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class DarajaTest {

    private final OkHttpClient okHttpClient;
    Logger logger = LoggerFactory.getLogger(DarajaTest.class);
    private DarajaApiService darajaApiService;

    public DarajaTest(DarajaApiService darajaApiService) {

        this.darajaApiService = darajaApiService;

        this.okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)  // Adjust connection timeout
                .writeTimeout(30, TimeUnit.SECONDS)    // Adjust write timeout
                .readTimeout(30, TimeUnit.SECONDS)     // Adjust read timeout
                .build();
    }

    public AccessTokenResponse getAccessToken(String token) {
        Request request = new Request.Builder()
                .url("/oauth/v1/generate?grant_type=client_credentials")
                .addHeader(HttpHeaders.AUTHORIZATION, "Basic " + token)
                .addHeader(HttpHeaders.CACHE_CONTROL, "no-cache")
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            assert response.body() != null;
            return HelperUtil.fromJson(response.body().string(), AccessTokenResponse.class);
        } catch (IOException e) {
            logger.error("Could not get access token {}", e.getLocalizedMessage());
            return null;
        }
    }

    public void printResponse(String secretKey) throws Exception {

    }

    public static void main(String[] args) {
        System.out.println("testing tu");
    }

}
