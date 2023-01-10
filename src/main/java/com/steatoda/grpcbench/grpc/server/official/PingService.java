package com.steatoda.grpcbench.grpc.server.official;

import com.google.protobuf.Empty;
import com.steatoda.grpcbench.proto.PingServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PingService extends PingServiceGrpc.PingServiceImplBase {

	@Override
	public void ping(Empty empty, StreamObserver<Empty> responseObserver) {

		Log.trace("Ping!");

		responseObserver.onNext(empty);

		responseObserver.onCompleted();

	}

	private static final Logger Log = LoggerFactory.getLogger(PingService.class);

}
