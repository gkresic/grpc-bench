package com.steatoda.grpcbench.grpc.server.vertx;

import com.steatoda.grpcbench.proto.PingServiceGrpc;
import com.steatoda.grpcbench.proto.Void;
import io.vertx.grpc.server.GrpcServer;
import io.vertx.grpc.server.GrpcServerRequest;
import io.vertx.grpc.server.GrpcServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PingService {

	public static void register(GrpcServer grpcServer) {

		grpcServer.callHandler(PingServiceGrpc.getPingMethod(), PingService::ping);

	}

	private static void ping(GrpcServerRequest<Void, Void> request) {

		Log.trace("Ping!");

		request.handler(nothing -> {

			GrpcServerResponse<Void, Void> response = request.response();

			response.end(nothing);

		});

	}

	private static final Logger Log = LoggerFactory.getLogger(PingService.class);

}
