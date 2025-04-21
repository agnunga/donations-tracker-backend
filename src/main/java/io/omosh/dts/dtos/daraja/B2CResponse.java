package io.omosh.dts.dtos.daraja;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class B2CResponse {

	@JsonProperty("Result")
	private Result result;
}