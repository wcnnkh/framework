package io.basc.framework.event.support;

import io.basc.framework.event.Event;
import io.basc.framework.util.concurrent.TaskQueue;

import java.util.concurrent.Executor;

public class SimpleAsyncStringNamedEventDispatcher<T extends Event> extends SimpleStringNamedEventDispatcher<T> {
	private Executor executor;

	public SimpleAsyncStringNamedEventDispatcher() {
		super(true);
		TaskQueue taskQueue = new TaskQueue();
		taskQueue.setName(getClass().getName());
		taskQueue.start();
		this.executor = taskQueue;
	}

	public SimpleAsyncStringNamedEventDispatcher(Executor executor) {
		super(true);
		this.executor = executor;
	}

	@Override
	public void publishEvent(String name, T event) {
		executor.execute(() -> super.publishEvent(name, event));
	}
}
