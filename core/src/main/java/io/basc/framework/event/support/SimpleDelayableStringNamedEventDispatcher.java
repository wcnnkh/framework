package io.basc.framework.event.support;

import io.basc.framework.core.Assert;
import io.basc.framework.event.DelayableNamedEventDispatcher;
import io.basc.framework.event.Event;
import io.basc.framework.util.concurrent.DefaultDelayableExecutor;
import io.basc.framework.util.concurrent.DelayableExecutor;

import java.util.concurrent.TimeUnit;

public class SimpleDelayableStringNamedEventDispatcher<T extends Event> extends
		SimpleStringNamedEventDispatcher<T> implements
		DelayableNamedEventDispatcher<String, T> {
	private final DelayableExecutor delayableExecutor;

	public SimpleDelayableStringNamedEventDispatcher() {
		this(new DefaultDelayableExecutor());
	}

	public SimpleDelayableStringNamedEventDispatcher(DelayableExecutor delayableExecutor) {
		super(true);
		this.delayableExecutor = delayableExecutor;
	}

	@Override
	public void publishEvent(String name, T event) {
		// 不直接publish的目的是为了让消息异步
		publishEvent(name, event, 0, TimeUnit.MILLISECONDS);
	}

	@Override
	public void publishEvent(String name, T event, long delay,
			TimeUnit delayTimeUnit) {
		Assert.requiredArgument(delay >= 0, "delay");
		delayableExecutor.schedule(() -> {
			super.publishEvent(name, event);
		}, delay, delayTimeUnit);
	}

}
