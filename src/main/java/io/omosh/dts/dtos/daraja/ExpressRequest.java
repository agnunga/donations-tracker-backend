package io.omosh.dts.dtos.daraja;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ExpressRequest{

	@JsonProperty("TransactionType")
	private String transactionType;

	@JsonProperty("Amount")
	private int amount;

	@JsonProperty("CallBackURL")
	private String callBackURL;

	@JsonProperty("PhoneNumber")
	private long phoneNumber;

	@JsonProperty("PartyA")
	private long partyA;

	@JsonProperty("PartyB")
	private int partyB;

	@JsonProperty("AccountReference")
	private String accountReference;

	@JsonProperty("TransactionDesc")
	private String transactionDesc;

	@JsonProperty("BusinessShortCode")
	private int businessShortCode;

	@JsonProperty("Timestamp")
	private String timestamp;

	@JsonProperty("Password")
	private String password;
}