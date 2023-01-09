package com.steatoda.grpcbench.rest.client;

import com.steatoda.grpcbench.proto.Payload;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import retrofit2.Response;
import retrofit2.Retrofit;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.OutputStream;

public class EatRestService {

	public EatRestService(Retrofit retrofit) {
		this.api = retrofit.create(EatRestApi.class);
	}

	public Payload eat(Payload[] payloads) {

		RequestBody requestBody = new RequestBody() {
			@Nullable
			@Override
			public MediaType contentType() {
				return MediaType.parse("application/protobuf");
			}
			@Override
			public void writeTo(BufferedSink sink) throws IOException {
				OutputStream ostream = new OutputStream() {
					@Override
					public void write(int b) throws IOException {
						sink.writeByte(b);
					}
					@Override
					public void write(byte[] b) throws IOException {
						sink.write(b);
					}
					@Override
					public void write(byte[] b, int off, int len) throws IOException {
						sink.write(b, off, len);
					}
				};
				for (Payload payload : payloads)
					payload.writeDelimitedTo(ostream);
			}

		};

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
