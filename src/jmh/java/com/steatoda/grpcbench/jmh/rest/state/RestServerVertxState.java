package com.steatoda.grpcbench.jmh.rest.state;

import com.steatoda.grpcbench.rest.server.vertx.VertxServer;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class RestServerVertxState {

	public RestServerVertxState() {

		try {
			vertxServer = new VertxServer();
		} catch (Exception e) {
			throw new RuntimeException("Error initializing Vert.x REST server", e);
		}

	}

	public void stop() {

		try {
			vertxServer.stop();
		} catch (Exception e) {
			throw new RuntimeException("Error stopping Vert.x REST server", e);
		}

	}

	final VertxServer vertxServer;

}
