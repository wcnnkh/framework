package io.basc.framework.util.actor;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public interface ListenableFuture<V> extends ListenableReceipt<ListenableFuture<? extends V>>, Future<V> {

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
