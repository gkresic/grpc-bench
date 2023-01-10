package com.steatoda.grpcbench.jmh.grpc;

import com.steatoda.grpcbench.jmh.grpc.state.GrpcClientState;
import com.steatoda.grpcbench.jmh.grpc.state.GrpcServerVertxState;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrpcVertx_3_EatStream extends GrpcEatStreamBase {

	@Benchmark
	public void benchmark(GrpcServerVertxState serverState, GrpcClientState clientState, Blackhole blackhole) {
		super.benchmark(clientState, blackhole);
	}

	@TearDown(Level.Trial)
	public void cleanup(GrpcServerVertxState serverState) {
		serverState.stop();
	}

	@SuppressWarnings("unused")
	private static final Logger Log = LoggerFactory.getLogger(GrpcVertx_3_EatStream.class);

}
