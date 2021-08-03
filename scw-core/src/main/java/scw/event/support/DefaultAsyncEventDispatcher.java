package scw.event.support;

import java.util.concurrent.Executor;

import scw.event.Event;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.util.task.support.TaskExecutors;

public class DefaultAsyncEventDispatcher<T extends Event> extends DefaultEventDispatcher<T> {
	private Executor executor;

	public DefaultAsyncEventDispatcher() {
		this(TaskExecutors.getGlobalExecutor());
	}

	public DefaultAsyncEventDispatcher(Executor executor) {
		super(true);
		this.executor = executor;
	}

	@Override
	public void publishEvent(T event) {
		executor.execute(() -> super.publishEvent(event));
	}

	@Override
	public EventRegistration registerListener(EventListener<T> eventListener) {
		return super.registerListener((event) -> executor.execute(() -> eventListener.onEvent(event)));
	}
}
