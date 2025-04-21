package io.omosh.dts.dtos.daraja;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SyncResponse{

	@JsonProperty("ConversationID")
	private String conversationID;

	@JsonProperty("ResponseCode")
	private String responseCode;

	@JsonProperty("OriginatorConversationID")
	private String originatorConversationID;

	@JsonProperty("ResponseDescription")
	private String responseDescription;
}