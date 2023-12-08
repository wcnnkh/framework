package io.basc.framework.event.support;

import java.util.concurrent.TimeUnit;

import io.basc.framework.event.batch.BatchEventListener;
import io.basc.framework.event.batch.DelayableBatchEventDispatcher;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.concurrent.DelayableExecutor;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.select.Selector;

public class DefaultDelayableEventDispatcher<T> extends DefaultEventDispatcher<T>
		implements DelayableBatchEventDispatcher<T> {
	private final DelayableExecutor delayableExecutor;

	public DefaultDelayableEventDispatcher(@Nullable Selector<BatchEventListener<T>> eventListenerSelector,
			DelayableExecutor delayableExecutor) {
		super(eventListenerSelector);
		Assert.requiredArgument(delayableExecutor != null, "delayableExecutor");
		this.delayableExecutor = delayableExecutor;
	}

	@Override
	public void publishBatchEvent(Elements<T> events) {
		// 不直接publish的目的是为了让消息异
		publishBatchEvent(events, 0, TimeUnit.MILLISECONDS);
	}

	@Override
	public void publishBatchEvent(Elements<T> events, long delay, TimeUnit delayTimeUnit) {
		Assert.requiredArgument(delay >= 0, "delay");
		delayableExecutor.schedule(() -> {
			super.publishBatchEvent(events);
		}, delay, delayTimeUnit);
	}

}
