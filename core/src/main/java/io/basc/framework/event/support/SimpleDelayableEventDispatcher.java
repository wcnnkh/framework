package io.basc.framework.event.support;

import io.basc.framework.event.DelayableEventDispatcher;
import io.basc.framework.event.Event;
import io.basc.framework.util.Assert;
import io.basc.framework.util.concurrent.DefaultDelayableExecutor;
import io.basc.framework.util.concurrent.DelayableExecutor;

import java.util.concurrent.TimeUnit;

public class SimpleDelayableEventDispatcher<T extends Event> extends SimpleEventDispatcher<T>
		implements DelayableEventDispatcher<T> {
	private final DelayableExecutor delayableExecutor;

	public SimpleDelayableEventDispatcher() {
		this(new DefaultDelayableExecutor());
	}

	public SimpleDelayableEventDispatcher(DelayableExecutor delayableExecutor) {
		this.delayableExecutor = delayableExecutor;
	}

	@Override
	public void publishEvent(T event) {
		// 不直接publish的目的是为了让消息异步
		publishEvent(event, 0, TimeUnit.MILLISECONDS);
	}

	@Override
	public void publishEvent(T event, long delay, TimeUnit delayTimeUnit) {
		Assert.requiredArgument(delay >= 0, "delay");
		delayableExecutor.schedule(() -> {
			super.publishEvent(event);
		}, delay, delayTimeUnit);
	}

}
