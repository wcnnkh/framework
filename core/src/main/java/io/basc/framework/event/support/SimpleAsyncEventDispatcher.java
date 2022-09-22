package io.basc.framework.event.support;

import java.util.concurrent.Executor;

import io.basc.framework.event.Event;
import io.basc.framework.util.concurrent.TaskQueue;

public class SimpleAsyncEventDispatcher<T extends Event> extends SimpleEventDispatcher<T> {
	private Executor executor;

	public SimpleAsyncEventDispatcher() {
		TaskQueue taskQueue = new TaskQueue();
		taskQueue.setName(getClass().getName());
		taskQueue.start();
		this.executor = taskQueue;
	}

	public SimpleAsyncEventDispatcher(Executor executor) {
		this.executor = executor;
	}

	@Override
	public void publishEvent(T event) {
		executor.execute(() -> super.publishEvent(event));
	}
}
