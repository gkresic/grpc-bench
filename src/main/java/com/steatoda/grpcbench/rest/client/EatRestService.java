package com.steatoda.grpcbench.rest.client;

import com.steatoda.grpcbench.proto.Payload;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EatRestService {

	public EatRestService(Retrofit retrofit) {
		this.api = retrofit.create(EatRestApi.class);
	}

	public Payload eat(Payload[] payloads) {

		RequestBody requestBody;
		try {
			ByteArrayOutputStream ostream = new ByteArrayOutputStream();
			for (Payload payload : payloads)
				payload.writeDelimitedTo(ostream);
			requestBody = RequestBody.create(MediaType.parse("application/protobuf"), ostream.toByteArray());
		} catch (IOException e) {
			throw new RuntimeException("Unable to construct request body", e);
		}

		Response<ResponseBody> response;
		try {
			response = api.eat(requestBody).execute();
		} catch (IOException e) {
			throw new RuntimeException("Error making a request", e);
		}

		if (!response.isSuccessful())
			throw new RuntimeException("Unsuccessful response " + response.code());

		try (ResponseBody responseBody = response.body()) {
			return Payload.parseFrom(responseBody.byteStream());
		} catch (IOException e) {
			throw new RuntimeException("Error parsing response", e);
		}

	}

	private final EatRestApi api;

}
