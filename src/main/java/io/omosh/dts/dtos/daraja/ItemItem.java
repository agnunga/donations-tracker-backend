package io.omosh.dts.dtos.daraja;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ItemItem{

	@JsonProperty("Value")
	private Object value;

	@JsonProperty("Name")
	private String name;
}