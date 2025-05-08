package io.omosh.dts.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TestRecord(

	@JsonProperty("role")
	String role,

	@JsonProperty("id")
	int id,

	@JsonProperty("fullname")
	String fullname,

	@JsonProperty("email")
	String email,

	@JsonProperty("username")
	String username,

	@JsonProperty("status")
	String status
) {
}