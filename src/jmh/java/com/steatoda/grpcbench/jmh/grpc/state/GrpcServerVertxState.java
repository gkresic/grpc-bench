package com.steatoda.grpcbench.jmh.grpc.state;

import com.steatoda.grpcbench.grpc.server.vertx.VertxServer;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class GrpcServerVertxState {

	public GrpcServerVertxState() {

		try {
			grpcServer = new VertxServer();
		} catch (Exception e) {
			throw new RuntimeException("Error initializing Vert.x gRPC server", e);
		}

	}

	public void stop() {

		try {
			grpcServer.stop();
		} catch (Exception e) {
			throw new RuntimeException("Error stopping Vert.x gRPC server", e);
		}

	}

	final VertxServer grpcServer;

}
