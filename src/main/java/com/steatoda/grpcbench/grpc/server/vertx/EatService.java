package com.steatoda.grpcbench.grpc.server.vertx;

import com.steatoda.grpcbench.proto.EatServiceGrpc;
import com.steatoda.grpcbench.proto.Payload;
import io.vertx.grpc.server.GrpcServer;
import io.vertx.grpc.server.GrpcServerRequest;
import io.vertx.grpc.server.GrpcServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

public class EatService {

	public static void register(GrpcServer grpcServer) {

		grpcServer.callHandler(EatServiceGrpc.getEatOneMethod(), EatService::eatOne);
		grpcServer.callHandler(EatServiceGrpc.getEatStreamMethod(), EatService::eatStream);

	}

	private static void eatOne(GrpcServerRequest<Payload, Payload> request) {

		request.handler(payload -> {

			GrpcServerResponse<Payload, Payload> response = request.response();

			response.end(payload);

		});

	}

	private static void eatStream(GrpcServerRequest<Payload, Payload> request) {

		AtomicReference<Payload.Builder> maxBuilderRef = new AtomicReference<>();

		request.handler(payload -> {

			Log.trace("Got streamed payload {}/{}", payload.getNumber(), payload.getText());

			if (maxBuilderRef.get() == null) {
				maxBuilderRef.set(Payload.newBuilder()
								 .setNumber(payload.getNumber())
								 .setText(payload.getText())
				);
				return;
			}

			if (maxBuilderRef.get().getNumber() < payload.getNumber())
				maxBuilderRef.get().setNumber(payload.getNumber());
			if (maxBuilderRef.get().getText().compareTo(payload.getText()) < 0)
				maxBuilderRef.get().setText(payload.getText());

		});

		request.exceptionHandler(error -> {
			Log.error("Error during lunch!", error);
		});

		request.endHandler(v ->{
			Payload max = maxBuilderRef.get().build();
			Log.trace("Returning max payload {}/{}", max.getNumber(), max.getText());
			request.response().end(max);
		});

	}

	private static final Logger Log = LoggerFactory.getLogger(EatService.class);

}
