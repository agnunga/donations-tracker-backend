package io.omosh.dts.dtos.daraja;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReversalResult{

	@JsonProperty("Result")
	private Result result;
}