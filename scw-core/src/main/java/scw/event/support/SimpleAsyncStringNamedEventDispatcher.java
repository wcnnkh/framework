package scw.event.support;

import java.util.concurrent.Executor;

import scw.event.Event;
import scw.util.concurrent.TaskQueue;

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
