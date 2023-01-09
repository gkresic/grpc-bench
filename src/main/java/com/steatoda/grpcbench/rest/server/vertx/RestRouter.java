package com.steatoda.grpcbench.rest.server.vertx;

import com.steatoda.grpcbench.proto.Payload;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.impl.RouterImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class RestRouter extends RouterImpl {

	public RestRouter(Vertx vertx) {

		super(vertx);

		post("/eat")
			.consumes("application/protobuf")
			.produces("application/protobuf")
			.handler(BodyHandler.create())
			.handler(this::eat);

	}

	private void eat(RoutingContext context) {

		byte[] body = context.body().buffer().getBytes();
		InputStream istream = new ByteArrayInputStream(body);

		Payload.Builder maxBuilder = null;

		try {

			Payload payload;
			while ((payload = Payload.parseDelimitedFrom(istream)) != null) {

				Log.trace("Got streamed payload {}/{}", payload.getNumber(), payload.getText());

				if (maxBuilder == null) {
					maxBuilder = Payload.newBuilder()
									 .setNumber(payload.getNumber())
									 .setText(payload.getText())
					;
				} else {
					if (maxBuilder.getNumber() < payload.getNumber())
						maxBuilder.setNumber(payload.getNumber());
					if (maxBuilder.getText().compareTo(payload.getText()) < 0)
						maxBuilder.setText(payload.getText());
				}

			}

		} catch (IOException e) {
			throw new RuntimeException("Error reading request body", e);
		}

		if (maxBuilder == null)
			throw new RuntimeException("No payloads in request?!");

		context.response()
			.putHeader(HttpHeaders.CONTENT_TYPE, "application/protobuf")
			.end(Buffer.buffer(maxBuilder.build().toByteArray()));

	}

	private static final Logger Log = LoggerFactory.getLogger(RestRouter.class);

}
