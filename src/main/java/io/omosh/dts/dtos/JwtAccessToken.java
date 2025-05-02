package io.omosh.dts.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class JwtAccessToken {

    @JsonProperty("token")
    private String token;

    @JsonProperty("expires_in")
    private long expiresIn;

    public JwtAccessToken() {
    }

    public JwtAccessToken(String token, long expiresIn) {
        this.token = token;
        this.expiresIn = expiresIn;
    }
}
