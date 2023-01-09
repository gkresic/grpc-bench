package com.steatoda.grpcbench.grpc.server.official;

import com.steatoda.grpcbench.grpc.GrpcParams;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GrpcServer {

	public GrpcServer() throws IOException {

		Server server = ServerBuilder.forPort(GrpcParams.Port)
							.addService(new PingService())
							.addService(new EatService())
							.build();

		server.start();

		Log.debug("GrpcServer constructed");

	}

	private static final Logger Log = LoggerFactory.getLogger(GrpcServer.class);

}
