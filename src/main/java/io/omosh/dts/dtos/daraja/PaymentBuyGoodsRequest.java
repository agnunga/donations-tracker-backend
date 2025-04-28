package io.omosh.dts.dtos.daraja;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PaymentBuyGoodsRequest{

	@JsonProperty("QueueTimeOutURL")
	private String queueTimeOutURL;

	@JsonProperty("Amount")
	private String amount;

	@JsonProperty("RecieverIdentifierType")
	private String receiverIdentifierType;

	@JsonProperty("PartyA")
	private String partyA;

	@JsonProperty("PartyB")
	private String partyB;

	@JsonProperty("ResultURL")
	private String resultURL;

	@JsonProperty("Requester")
	private String requester;

	@JsonProperty("SenderIdentifierType")
	private String senderIdentifierType;

	@JsonProperty("Initiator")
	private String initiator;

	@JsonProperty("Remarks")
	private String remarks;

	@JsonProperty("SecurityCredential")
	private String securityCredential;

	@JsonProperty("AccountReference")
	private String accountReference;

	@JsonProperty("Command ID")
	private String commandID;
}