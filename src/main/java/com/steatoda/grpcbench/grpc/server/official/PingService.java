package com.steatoda.grpcbench.grpc.server.official;

import com.steatoda.grpcbench.proto.PingServiceGrpc;
import com.steatoda.grpcbench.proto.Void;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PingService extends PingServiceGrpc.PingServiceImplBase {

	@Override
	public void ping(Void nothing, StreamObserver<Void> responseObserver) {

		Log.trace("Ping!");

		responseObserver.onNext(nothing);

		responseObserver.onCompleted();

	}

	private static final Logger Log = LoggerFactory.getLogger(PingService.class);

}
