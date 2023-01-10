package com.steatoda.grpcbench.jmh.grpc;

import com.fasterxml.jackson.core.JsonParser;
import com.steatoda.grpcbench.JsonUtil;
import com.steatoda.grpcbench.jmh.JsonPayload;
import com.steatoda.grpcbench.jmh.grpc.state.GrpcClientState;
import com.steatoda.grpcbench.proto.Payload;
import io.grpc.stub.StreamObserver;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@State(Scope.Benchmark)
public class GrpcEatStreamBase {

	@Setup
    public void setup() {

		try (
			InputStream istream = Thread.currentThread().getContextClassLoader().getResourceAsStream(payloadFileName);
			JsonParser jsonParser = JsonUtil.jsonMapper().createParser(istream);
		) {

			payloads = Arrays.stream(jsonParser.readValueAs(JsonPayload[].class))
						   .map(jsonPayload -> Payload.newBuilder()
												   .setText(jsonPayload.text)
												   .setNumber(jsonPayload.number)
												   .build()
						   )
						   .toArray(Payload[]::new);

			if (payloads.length == 0)
				throw new RuntimeException("No payloads in JSON?!");

			maxText = payloads[0].getText();
			maxNumber = payloads[0].getNumber();
			for (int i = 1; i < payloads.length; ++i) {
				if (maxNumber < payloads[i].getNumber())
					maxNumber = payloads[i].getNumber();
				if (maxText.compareTo(payloads[i].getText()) < 0)
					maxText = payloads[i].getText();
			}

		} catch (IOException e) {
			throw new RuntimeException("Unable to load payloads from " + payloadFileName, e);
		}

	}

	public void benchmark(GrpcClientState clientState, Blackhole blackhole) {

		CountDownLatch finishLatch = new CountDownLatch(1);
		AtomicReference<Throwable> error = new AtomicReference<>();

		StreamObserver<Payload> responseObserver = new StreamObserver<>() {
			@Override
			public void onNext(Payload payload) {
				Log.trace("Got max {}/{}", payload.getNumber(), payload.getText());
				if (maxNumber != payload.getNumber() || !maxText.equals(payload.getText()))
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

		StreamObserver<Payload> requestObserver = clientState.client().eatStub().eatStream(responseObserver);
		try {
			for (Payload payload : payloads) {
				Log.trace("Sending {}/{}", payload.getNumber(), payload.getText());
				requestObserver.onNext(payload);
				if (finishLatch.getCount() == 0)
					return;
			}
		} catch (Exception e) {
			// cancel RPC
			requestObserver.onError(e);
			throw e;
		}

		requestObserver.onCompleted();

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

	private static final Logger Log = LoggerFactory.getLogger(GrpcEatStreamBase.class);

	@Param({"payload-10.json", "payload-100.json"})
	String payloadFileName;

	private Payload[] payloads;
	private String maxText;
	private int maxNumber;

}
