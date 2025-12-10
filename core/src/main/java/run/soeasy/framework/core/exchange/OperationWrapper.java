package run.soeasy.framework.core.exchange;

import java.util.concurrent.TimeUnit;

import run.soeasy.framework.core.domain.Wrapper;

public interface OperationWrapper<W extends Operation> extends Operation, Wrapper<W> {
	@Override
	default boolean cancel() {
		return getSource().cancel();
	}

	@Override
	default Throwable cause() {
		return getSource().cause();
	}

	@Override
	default boolean isCancellable() {
		return getSource().isCancellable();
	}

	@Override
	default boolean isCancelled() {
		return getSource().isCancelled();
	}

	@Override
	default boolean isDone() {
		return getSource().isDone();
	}

	@Override
	default boolean isRollback() {
		return getSource().isRollback();
	}

	@Override
	default boolean isRollbackSupported() {
		return getSource().isRollbackSupported();
	}

	@Override
	default boolean isSuccess() {
		return getSource().isSuccess();
	}

	@Override
	default boolean rollback() {
		return getSource().rollback();
	}

	@Override
	default Operation sync() {
		return getSource().sync();
	}

	@Override
	default Operation sync(long timeout, TimeUnit unit) {
		return getSource().sync(timeout, unit);
	}

	@Override
	default void await() throws InterruptedException {
		getSource().await();
	}

	@Override
	default boolean await(long timeout, TimeUnit unit) throws InterruptedException {
		return getSource().await(timeout, unit);
	}
}
