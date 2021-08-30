package io.basc.framework.event.support;

import io.basc.framework.event.Event;
import io.basc.framework.util.concurrent.TaskQueue;

import java.util.concurrent.Executor;

public class SimpleAsyncEventDispatcher<T extends Event> extends
		SimpleEventDispatcher<T> {
	private Executor executor;

	public SimpleAsyncEventDispatcher() {
		super(true);
		TaskQueue taskQueue = new TaskQueue();
		taskQueue.setName(getClass().getName());
		taskQueue.start();
		this.executor = taskQueue;
	}

	public SimpleAsyncEventDispatcher(Executor executor) {
		super(true);
		this.executor = executor;
	}
	
	@Override
	public void publishEvent(T event) {
		executor.execute(() -> super.publishEvent(event));
	}
}
