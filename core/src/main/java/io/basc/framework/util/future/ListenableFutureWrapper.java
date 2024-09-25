package io.basc.framework.util.future;

import java.util.concurrent.TimeUnit;

import io.basc.framework.util.actor.ListenableReceiptWrapper;
import io.basc.framework.util.actor.ListenableFuture;

public interface ListenableFutureWrapper<V, W extends ListenableFuture<V>>
		extends ListenableFuture<V>, ListenableReceiptWrapper<ListenableFuture<? extends V>, W> {

	@Override
	default V getNow() {
		return getSource().getNow();
	}

	@Override
	default boolean cancel() {
		return getSource().cancel();
	}

	@Override
	default boolean await(long timeout, TimeUnit unit) throws InterruptedException {
		return getSource().await(timeout, unit);
	}

	@Override
	default boolean isDone() {
		return getSource().isDone();
	}

	@Override
	default boolean isCancelled() {
		return getSource().isCancellable();
	}
}
