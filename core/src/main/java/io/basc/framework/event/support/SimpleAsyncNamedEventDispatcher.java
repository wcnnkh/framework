package io.basc.framework.event.support;

import io.basc.framework.event.Event;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Matcher;
import io.basc.framework.util.concurrent.TaskQueue;

import java.util.concurrent.Executor;

public class SimpleAsyncNamedEventDispatcher<K, T extends Event> extends SimpleNamedEventDispatcher<K, T> {
	private Executor executor;

	public SimpleAsyncNamedEventDispatcher() {
		this(null);
	}

	public SimpleAsyncNamedEventDispatcher(@Nullable Matcher<K> matcher) {
		super(matcher);
		TaskQueue taskQueue = new TaskQueue();
		taskQueue.setName(getClass().getName());
		taskQueue.start();
		this.executor = taskQueue;
	}

	public SimpleAsyncNamedEventDispatcher(@Nullable Matcher<K> matcher, Executor executor) {
		super(matcher);
		Assert.requiredArgument(executor != null, "executor");
		this.executor = executor;
	}

	@Override
	public void publishEvent(K name, T event) {
		executor.execute(() -> super.publishEvent(name, event));
	}
}
