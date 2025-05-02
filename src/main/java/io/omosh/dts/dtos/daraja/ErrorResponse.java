package io.omosh.dts.dtos.daraja;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ErrorResponse{

	@JsonProperty("requestId")
	private String requestId;

	@JsonProperty("errorMessage")
	private String errorMessage;

	@JsonProperty("errorCode")
	private String errorCode;
}