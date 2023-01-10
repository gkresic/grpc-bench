package com.steatoda.grpcbench.jmh.rest.state;

import com.steatoda.grpcbench.rest.client.RestClient;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Thread)
public class RestClientState {

	public RestClientState() {

		restClient = new RestClient();

	}

	public RestClient client() { return restClient; }

	private final RestClient restClient;
	
}
