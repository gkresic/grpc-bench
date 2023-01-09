package com.steatoda.grpcbench.rest.server.vertx;

import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VertxServer {

	public VertxServer() throws InterruptedException {

		VertxOptions vertxOptions = new VertxOptions();

		vertx = Vertx.vertx(vertxOptions);

		awaitComplete(CompositeFuture.all(
			Stream.of(
				RestVerticle.deploy(vertx)
			)
			.collect(Collectors.toList())
		));

		Log.debug("Vert.x server constructed");

	}

	public void stop() {

		try {
			Promise<Void> vertxClosePromise = Promise.promise();
			vertx.close(result -> {
				if (result.succeeded())
					vertxClosePromise.complete(result.result());
				else
					vertxClosePromise.fail(result.cause());
			});
			awaitComplete(vertxClosePromise.future());
		} catch (Exception e) {
			Log.error("Error closing Vert.x", e);
		}

		Log.debug("Vert.x closed");

	}

	// adopted from https://stackoverflow.com/a/68543780/4553548
	private static <T> T awaitComplete(io.vertx.core.Future<T> future) throws InterruptedException {

		final Object lock = new Object();
		final AtomicReference<AsyncResult<T>> resultRef = new AtomicReference<>(null);

		T result;
		Throwable cause;

		if (future.isComplete()) {
			result = future.result();
			cause = future.cause();
		} else {
			synchronized (lock) {
				// We *must* be locked before registering a callback.
				// If result is ready, the callback is called immediately!
				future.onComplete((AsyncResult<T> result2) -> {
					resultRef.set(result2);
					synchronized (lock) {
						lock.notify();
					}
				});
				while (resultRef.get() == null) {
					// Nested sync on lock is fine.  If we get a spurious wake-up before resultRef is set, we need to reacquire the lock, then wait again.
					// Ref: https://stackoverflow.com/a/249907/257299
					synchronized (lock) {
						// @Blocking
						lock.wait();
					}
				}
			}
			AsyncResult<T> asyncResult = resultRef.get();
			result = asyncResult.result();
			cause = asyncResult.cause();
		}

		if (cause != null)
			throw new RuntimeException("Future failed", cause);

		return result;

	}

	private static final Logger Log = LoggerFactory.getLogger(VertxServer.class);

	private final Vertx vertx;

}
