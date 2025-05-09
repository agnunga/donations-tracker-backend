package io.omosh.dts.dtos.daraja;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ExpressResponse {

    @JsonProperty("MerchantRequestID")
    private String merchantRequestID;

    @JsonProperty("ResponseCode")
    private String responseCode;

    @JsonProperty("CustomerMessage")
    private String customerMessage;

    @JsonProperty("CheckoutRequestID")
    private String checkoutRequestID;

    @JsonProperty("ResponseDescription")
    private String responseDescription;

    public ExpressResponse() {
    }

    public ExpressResponse(String responseDescription) {
        this.responseDescription = responseDescription;
    }
}