package com.steatoda.grpcbench.jmh.grpc;

import com.steatoda.grpcbench.jmh.grpc.state.GrpcClientState;
import com.steatoda.grpcbench.jmh.grpc.state.GrpcServerOfficialState;
import com.steatoda.grpcbench.proto.Void;
import io.grpc.stub.StreamObserver;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@State(Scope.Benchmark)
public class GrpcOfficialBenchmark_1_Ping {

	@Setup
    public void setup() {

		nothing = Void.newBuilder().build();

	}

	@Benchmark
	public void benchmark(GrpcServerOfficialState serverState, GrpcClientState clientState, Blackhole blackhole) {

		CountDownLatch finishLatch = new CountDownLatch(1);
		AtomicReference<Throwable> error = new AtomicReference<>();

		StreamObserver<Void> responseObserver = new StreamObserver<>() {
			@Override
			public void onNext(Void nothing) {
				Log.trace("Got ping response");
				blackhole.consume(nothing);
			}
			@Override
			public void onError(Throwable t) {
				Log.error("Error during ping!", t);
				error.set(t);
				finishLatch.countDown();
			}
			@Override
			public void onCompleted() {
				Log.trace("Finished ping");
				finishLatch.countDown();
			}
		};

		clientState.client().pingStub().ping(nothing, responseObserver);

		// receiving happens asynchronously
		try {
			if (!finishLatch.await(1, TimeUnit.MINUTES))
				throw new RuntimeException("Timeout waiting for response!");
			if (error.get() != null)
				throw new RuntimeException("Error handling request", error.get());
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted while waiting for response");
		}

	}

	private static final Logger Log = LoggerFactory.getLogger(GrpcOfficialBenchmark_1_Ping.class);

	private Void nothing;

}
