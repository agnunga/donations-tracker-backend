package io.omosh.dts.dtos.daraja;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GenerateQrResponse{

	@JsonProperty("ResponseCode")
	private String responseCode;

	@JsonProperty("QRCode")
	private String qRCode;

	@JsonProperty("RequestID")
	private String requestID;

	@JsonProperty("ResponseDescription")
	private String responseDescription;

	public GenerateQrResponse(){

	}

	public GenerateQrResponse(String responseDescription){
		this.responseDescription = responseDescription;
	}
}