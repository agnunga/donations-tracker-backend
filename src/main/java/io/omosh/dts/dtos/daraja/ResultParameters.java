package io.omosh.dts.dtos.daraja;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResultParameters{

	@JsonProperty("ResultParameter")
	private List<ResultParameterItem> resultParameter;
}