package io.omosh.dts.dtos.daraja;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CallbackMetadata{

	@JsonProperty("Item")
	private List<ItemItem> item;
}