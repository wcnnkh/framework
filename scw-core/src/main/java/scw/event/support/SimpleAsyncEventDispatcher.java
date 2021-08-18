package scw.event.support;

import java.util.concurrent.Executor;

import scw.event.Event;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.util.concurrent.TaskQueue;

public class SimpleAsyncEventDispatcher<T extends Event> extends
		SimpleEventDispatcher<T> {
	private Executor executor;

	public SimpleAsyncEventDispatcher() {
		super(true);
		TaskQueue taskQueue = new TaskQueue();
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

	@Override
	public EventRegistration registerListener(EventListener<T> eventListener) {
		return super.registerListener((event) -> executor
				.execute(() -> eventListener.onEvent(event)));
	}
}
