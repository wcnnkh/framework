package io.basc.framework.event.support;

import io.basc.framework.event.Event;
import io.basc.framework.util.concurrent.TaskQueue;

import java.util.concurrent.Executor;

public class SimpleAsyncNamedEventDispatcher<K, T extends Event> extends SimpleNamedEventDispatcher<K, T> {
	private Executor executor;

	public SimpleAsyncNamedEventDispatcher() {
		super(true);
		TaskQueue taskQueue = new TaskQueue();
		taskQueue.setName(getClass().getName());
		taskQueue.start();
		this.executor = taskQueue;
	}

	public SimpleAsyncNamedEventDispatcher(Executor executor) {
		super(true);
		this.executor = executor;
	}

	@Override
	public void publishEvent(K name, T event) {
		executor.execute(() -> super.publishEvent(name, event));
	}
}
