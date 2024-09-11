package io.basc.framework.util.observe.future;

import java.util.concurrent.TimeUnit;

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
