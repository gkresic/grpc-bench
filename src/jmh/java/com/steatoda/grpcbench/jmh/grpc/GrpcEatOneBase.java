package com.steatoda.grpcbench.jmh.grpc;

import com.steatoda.grpcbench.jmh.grpc.state.GrpcClientState;
import com.steatoda.grpcbench.proto.Payload;
import io.grpc.stub.StreamObserver;
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
public abstract class GrpcEatOneBase {

	public static final String PayloadText = "foo";
	public static final int PayloadNumber = 42;

	@Setup
    public void setup() {

		payload = Payload.newBuilder()
					  .setText(PayloadText)
					  .setNumber(PayloadNumber)
					  .build();

	}

	public void benchmark(GrpcClientState clientState, Blackhole blackhole) {

		CountDownLatch finishLatch = new CountDownLatch(1);
		AtomicReference<Throwable> error = new AtomicReference<>();

		StreamObserver<Payload> responseObserver = new StreamObserver<>() {
			@Override
			public void onNext(Payload payload) {
				Log.trace("Got {}/{}", payload.getNumber(), payload.getText());
				if (PayloadNumber != payload.getNumber() || !PayloadText.equals(payload.getText()))
					error.set(new RuntimeException("Invalid payload returned: " + payload));
				blackhole.consume(payload);
			}
			@Override
			public void onError(Throwable t) {
				Log.error("Error during lunch!", t);
				error.set(t);
				finishLatch.countDown();
			}
			@Override
			public void onCompleted() {
				Log.trace("Finished lunch");
				finishLatch.countDown();
			}
		};

		clientState.client().eatStub().eatOne(payload, responseObserver);

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

	private static final Logger Log = LoggerFactory.getLogger(GrpcEatOneBase.class);

	private Payload payload;

}
