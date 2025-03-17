package run.soeasy.framework.util.exchange;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import run.soeasy.framework.util.exchange.future.ListenableFutureTask;

public abstract class AbstractChannel<T> implements Channel<T> {
	private Executor publishExecutor;

	public Executor getPublishExecutor() {
		return publishExecutor;
	}

	public void setPublishExecutor(Executor publishExecutor) {
		this.publishExecutor = publishExecutor;
	}

	@Override
	public Receipt publish(T resource, long timeout, TimeUnit timeUnit) {
		if (publishExecutor == null) {
			syncPublish(resource);
			return Receipt.SUCCESS;
		} else {
			ListenableFutureTask<?> task = new ListenableFutureTask<>(() -> syncPublish(resource), null);
			publishExecutor.execute(task);
			return task;
		}
	}

	public abstract void syncPublish(T resource);
}
