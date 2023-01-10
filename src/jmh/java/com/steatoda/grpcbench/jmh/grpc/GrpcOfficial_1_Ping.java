package com.steatoda.grpcbench.jmh.grpc;

import com.steatoda.grpcbench.jmh.grpc.state.GrpcClientState;
import com.steatoda.grpcbench.jmh.grpc.state.GrpcServerOfficialState;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrpcOfficial_1_Ping extends GrpcPingBase {

	@Benchmark
	public void benchmark(GrpcServerOfficialState serverState, GrpcClientState clientState, Blackhole blackhole) {
		super.benchmark(clientState, blackhole);
	}

	@SuppressWarnings("unused")
	private static final Logger Log = LoggerFactory.getLogger(GrpcOfficial_1_Ping.class);

}
