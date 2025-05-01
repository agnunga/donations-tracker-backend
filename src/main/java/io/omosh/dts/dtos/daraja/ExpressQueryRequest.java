package io.omosh.dts.dtos.daraja;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ExpressQueryRequest{

	@JsonProperty("CheckoutRequestID")
	private String checkoutRequestID;

	@JsonProperty("BusinessShortCode")
	private String businessShortCode;

	@JsonProperty("Timestamp")
	private String timestamp;

	@JsonProperty("Password")
	private String password;
}