package io.basc.framework.event.support;

import java.util.concurrent.TimeUnit;

import io.basc.framework.event.DelayableEventDispatcher;
import io.basc.framework.event.EventListener;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.concurrent.DelayableExecutor;
import io.basc.framework.util.select.Selector;

public class StandardDelayableEventDispatcher<T> extends StandardEventDispatcher<T>
		implements DelayableEventDispatcher<T> {
	private final DelayableExecutor delayableExecutor;

	public StandardDelayableEventDispatcher(@Nullable Selector<EventListener<T>> selector,
			DelayableExecutor delayableExecutor) {
		super(selector, null);
		Assert.requiredArgument(delayableExecutor != null, "delayableExecutor");
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
