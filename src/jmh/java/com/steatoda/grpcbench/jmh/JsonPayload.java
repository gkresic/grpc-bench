package com.steatoda.grpcbench.jmh;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JsonPayload {

	@JsonProperty
	public String text;

	@JsonProperty
	public Integer number;

}
