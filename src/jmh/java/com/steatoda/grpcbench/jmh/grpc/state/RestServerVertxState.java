package com.steatoda.grpcbench.jmh.grpc.state;

import com.steatoda.grpcbench.rest.server.vertx.VertxServer;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class RestServerVertxState {

	public RestServerVertxState() {

		try {
			vertxServer = new VertxServer();
		} catch (Exception e) {
			throw new RuntimeException("Error initializing VertxServer", e);
		}

	}

	public void stop() {
		vertxServer.stop();
	}

	final VertxServer vertxServer;

}
