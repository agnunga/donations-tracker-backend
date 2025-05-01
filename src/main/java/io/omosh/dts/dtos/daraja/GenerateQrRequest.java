package io.omosh.dts.dtos.daraja;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GenerateQrRequest{

	@JsonProperty("MerchantName")
	private String merchantName;

	@JsonProperty("RefNo")
	private String refNo;

	@JsonProperty("Size")
	private String size;

	@JsonProperty("Amount")
	private String amount;

	@JsonProperty("TrxCode")
	private String trxCode;

	@JsonProperty("CPI")
	private String cPI;
}