package com.steatoda.grpcbench.jmh.grpc.state;

import com.steatoda.grpcbench.grpc.server.official.GrpcServer;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.io.IOException;

@State(Scope.Benchmark)
public class GrpcServerOfficialState {

	public GrpcServerOfficialState() {

		try {
			grpcServer = new GrpcServer();
		} catch (IOException e) {
			throw new RuntimeException("Error initializing GrpcServer", e);
		}

	}

	final GrpcServer grpcServer;

}
