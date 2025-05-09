package io.omosh.dts.dtos.daraja;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class QueryBalanceRequest{

	@JsonProperty("QueueTimeOutURL")
	private String queueTimeOutURL;

	@JsonProperty("Initiator")
	private String initiator;

	@JsonProperty("Remarks")
	private String remarks;

	@JsonProperty("SecurityCredential")
	private String securityCredential;

	@JsonProperty("PartyA")
	private String partyA;

	@JsonProperty("IdentifierType")
	private String identifierType;

	@JsonProperty("CommandID")
	private String commandID;

	@JsonProperty("ResultURL")
	private String resultURL;
}