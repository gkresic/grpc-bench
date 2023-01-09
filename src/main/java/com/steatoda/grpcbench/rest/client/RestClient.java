package com.steatoda.grpcbench.rest.client;

import com.steatoda.grpcbench.rest.server.vertx.RestVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Retrofit;

public class RestClient {

	public RestClient() {

		Retrofit.Builder builder = new Retrofit.Builder()
									   .baseUrl("http://localhost:" + RestVerticle.Port)
		;

		retrofit = builder.build();

		eatService = new EatRestService(retrofit);

	}

	public EatRestService eatService() { return eatService; }

	private static final Logger Log = LoggerFactory.getLogger(RestClient.class);

	private final Retrofit retrofit;
	private final EatRestService eatService;

}
