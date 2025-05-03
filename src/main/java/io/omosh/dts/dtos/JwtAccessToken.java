package io.omosh.dts.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class JwtAccessToken {

    @JsonProperty("refresh-token")
    private String refreshToken;

    @JsonProperty("token")
    private String token;

    @JsonProperty("jti")
    private String jti;

    @JsonProperty("sub")
    private String sub;

    @JsonProperty("iat")
    private String iat;

    @JsonProperty("exp")
    private String exp;

    @JsonProperty("role")
    private String role;

    public JwtAccessToken() {
    }

    public JwtAccessToken(String refreshToken, String token, String sub, String iat, String jti, String exp, String role) {
        this.refreshToken = refreshToken;
        this.token = token;
        this.sub = sub;
        this.iat = iat;
        this.jti = jti;
        this.exp = exp;
        this.role = role;
    }

    public JwtAccessToken(String token, String exp) {
        this.token = token;
        this.exp = exp;
    }
}
