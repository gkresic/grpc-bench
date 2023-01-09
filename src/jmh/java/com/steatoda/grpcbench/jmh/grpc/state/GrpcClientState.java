package com.steatoda.grpcbench.jmh.grpc.state;

import com.steatoda.grpcbench.grpc.client.GrpcClient;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Thread)
public class GrpcClientState {

	public GrpcClientState() {

		grpcClient = new GrpcClient();

	}

	public GrpcClient client() { return grpcClient; }

	private final GrpcClient grpcClient;
	
}
