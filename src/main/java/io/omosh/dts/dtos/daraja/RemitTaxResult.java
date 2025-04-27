package io.omosh.dts.dtos.daraja;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RemitTaxResult {

	@JsonProperty("Result")
	private Result result;
}