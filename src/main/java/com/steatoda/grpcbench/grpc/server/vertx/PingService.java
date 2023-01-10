package com.steatoda.grpcbench.grpc.server.vertx;

import com.google.protobuf.Empty;
import com.steatoda.grpcbench.proto.PingServiceGrpc;
import io.vertx.grpc.server.GrpcServer;
import io.vertx.grpc.server.GrpcServerRequest;
import io.vertx.grpc.server.GrpcServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PingService {

	public static void register(GrpcServer grpcServer) {

		grpcServer.callHandler(PingServiceGrpc.getPingMethod(), PingService::ping);

	}

	private static void ping(GrpcServerRequest<Empty, Empty> request) {

		Log.trace("Ping!");

		request.handler(empty -> {

			GrpcServerResponse<Empty, Empty> response = request.response();

			response.end(empty);

		});

	}

	private static final Logger Log = LoggerFactory.getLogger(PingService.class);

}
