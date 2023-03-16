package io.basc.framework.event.support;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import io.basc.framework.event.DelayableNamedEventDispatcher;
import io.basc.framework.event.EventDispatcher;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Matcher;
import io.basc.framework.util.concurrent.DelayableExecutor;

public class StandardDelayableNamedEventDispatcher<K, T> extends StandardNamedEventDispatcher<K, T>
		implements DelayableNamedEventDispatcher<K, T> {
	private final DelayableExecutor delayableExecutor;

	public StandardDelayableNamedEventDispatcher(Function<? super K, ? extends EventDispatcher<T>> creator,
			@Nullable Matcher<K> matcher, DelayableExecutor delayableExecutor) {
		super(creator, matcher, null);
		Assert.requiredArgument(delayableExecutor != null, "delayableExecutor");
		this.delayableExecutor = delayableExecutor;
	}

	@Override
	public void publishEvent(K name, T event) {
		// 不直接publish的目的是为了让消息异步
		publishEvent(name, event, 0, TimeUnit.MILLISECONDS);
	}

	@Override
	public void publishEvent(K name, T event, long delay, TimeUnit delayTimeUnit) {
		Assert.requiredArgument(delay >= 0, "delay");
		delayableExecutor.schedule(() -> {
			super.publishEvent(name, event);
		}, delay, delayTimeUnit);
	}

}
