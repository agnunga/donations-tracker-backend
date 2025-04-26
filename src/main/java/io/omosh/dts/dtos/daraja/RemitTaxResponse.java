package io.omosh.dts.dtos.daraja;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RemitTaxResponse {

	@JsonProperty("Result")
	private Result result;
}