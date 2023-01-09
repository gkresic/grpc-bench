package com.steatoda.grpcbench.jmh.grpc;

import com.fasterxml.jackson.core.JsonParser;
import com.steatoda.grpcbench.JsonUtil;
import com.steatoda.grpcbench.jmh.JsonPayload;
import com.steatoda.grpcbench.jmh.grpc.state.RestClientState;
import com.steatoda.grpcbench.jmh.grpc.state.RestServerVertxState;
import com.steatoda.grpcbench.proto.Payload;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@State(Scope.Benchmark)
public class RestVertxBenchmark_EatStream {

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

	@Benchmark
	public void benchmark(RestServerVertxState serverState, RestClientState clientState, Blackhole blackhole) {

		Payload max = clientState.client().eatService().eat(payloads);

		Log.trace("Got max {}/{}", max.getNumber(), max.getText());

		if (maxNumber != max.getNumber() || !maxText.equals(max.getText()))
			throw new RuntimeException("Invalid payload returned: " + max);

		blackhole.consume(max);

	}

	@TearDown(Level.Trial)
	public void cleanup(RestServerVertxState serverState) {
		serverState.stop();
	}

	private static final Logger Log = LoggerFactory.getLogger(RestVertxBenchmark_EatStream.class);

	@Param({"payload-10.json", "payload-100.json"})
	String payloadFileName;

	private Payload[] payloads;
	private String maxText;
	private int maxNumber;

}
