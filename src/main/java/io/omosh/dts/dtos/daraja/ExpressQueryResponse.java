package io.omosh.dts.dtos.daraja;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ExpressQueryResponse {

    @JsonProperty("MerchantRequestID")
    private String merchantRequestID;

    @JsonProperty("ResponseCode")
    private String responseCode;

    @JsonProperty("CheckoutRequestID")
    private String checkoutRequestID;

    @JsonProperty("ResponseDescription")
    private String responseDescription;

    @JsonProperty("ResultDesc")
    private String resultDesc;

    @JsonProperty("ResultCode")
    private String resultCode;

    public ExpressQueryResponse() {

    }

    public ExpressQueryResponse(String responseDescription) {
        this.responseDescription = responseDescription;
    }
}
