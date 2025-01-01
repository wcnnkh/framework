package io.basc.framework.util.exchange.future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public interface ListenableFuture<V> extends ListenableReceipt<ListenableFuture<? extends V>>, Future<V> {

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

	@Override
	default boolean cancel() {
		return cancel(false);
	}

	/**
	 * 立刻获取结果，如果还未执行完或失败则返回空。但成功的结果集也可能为空所以应该综合{@link #isSuccess()}的结果
	 * 
	 * @return
	 */
	V getNow();

	@Override
	default boolean await(long timeout, TimeUnit unit) throws InterruptedException {
		try {
			get(timeout, unit);
			return true;
		} catch (ExecutionException | TimeoutException e) {
			return false;
		}
	}
}
