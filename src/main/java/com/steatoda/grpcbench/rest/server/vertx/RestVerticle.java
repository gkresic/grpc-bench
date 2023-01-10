package com.steatoda.grpcbench.rest.server.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.impl.cpu.CpuCoreSensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestVerticle extends AbstractVerticle {

	public static final int Port = 15002;

	public static Future<String> deploy(Vertx vertx) {
		DeploymentOptions options = new DeploymentOptions();
		options.setInstances(2 * CpuCoreSensor.availableProcessors());
		Promise<String> promise = Promise.promise();
		vertx.deployVerticle(() -> new RestVerticle(vertx), options, result -> {
			if (result.succeeded())
				promise.complete(result.result());
			else
				promise.fail(result.cause());
		});
		return promise.future();
	}

	public RestVerticle(Vertx vertx) {

		HttpServerOptions httpServerOptions = new HttpServerOptions()
												  .setPort(Port)
		;

		httpServer = vertx.createHttpServer(httpServerOptions);
		httpServer.requestHandler(new RestRouter(vertx));

	}

	@Override
	public void start(Promise<Void> startPromise) {
		httpServer.listen(result -> {
			if (result.succeeded())
				startPromise.complete();
			else
				startPromise.fail(result.cause());
		});
	}

	public void stop(Promise<Void> stopPromise) {
		httpServer.close(result -> {
			if (result.succeeded())
				stopPromise.complete();
			else
				stopPromise.fail(result.cause());
		});
	}

	@SuppressWarnings("unused")
	private static final Logger Log = LoggerFactory.getLogger(RestVerticle.class);

	private final HttpServer httpServer;

}
