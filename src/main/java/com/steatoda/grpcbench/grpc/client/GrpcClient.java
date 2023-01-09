package com.steatoda.grpcbench.grpc.client;

import com.steatoda.grpcbench.grpc.GrpcParams;
import com.steatoda.grpcbench.proto.EatServiceGrpc;
import com.steatoda.grpcbench.proto.PingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrpcClient {

	public GrpcClient() {

		ManagedChannel channel = ManagedChannelBuilder
					  .forAddress("localhost", GrpcParams.Port)
					  .usePlaintext()
					  .build();

		pingStub = PingServiceGrpc.newStub(channel);
		eatStub = EatServiceGrpc.newStub(channel);

		Log.debug("GrpcClient constructed");

	}

	public PingServiceGrpc.PingServiceStub pingStub() { return pingStub; }

	public EatServiceGrpc.EatServiceStub eatStub() { return eatStub; }

	private static final Logger Log = LoggerFactory.getLogger(GrpcClient.class);

	private final PingServiceGrpc.PingServiceStub pingStub;
	private final EatServiceGrpc.EatServiceStub eatStub;

}
