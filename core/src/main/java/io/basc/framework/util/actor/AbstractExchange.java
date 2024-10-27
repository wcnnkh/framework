package io.basc.framework.util.actor;

import java.util.concurrent.Executor;

import io.basc.framework.util.Receipt;
import io.basc.framework.util.future.ListenableFutureTask;

public abstract class AbstractExchange<T> implements Exchange<T> {
	private Executor publishExecutor;

	public Executor getPublishExecutor() {
		return publishExecutor;
	}

	public void setPublishExecutor(Executor publishExecutor) {
		this.publishExecutor = publishExecutor;
	}

	@Override
	public Receipt publish(T resource) {
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
