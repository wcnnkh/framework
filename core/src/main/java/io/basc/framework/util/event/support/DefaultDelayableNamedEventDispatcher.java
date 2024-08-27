package io.basc.framework.util.event.support;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.util.concurrent.DelayableExecutor;
import io.basc.framework.util.event.DelayableNamedEventDispatcher;
import io.basc.framework.util.event.EventDispatcher;
import io.basc.framework.util.match.Matcher;
import io.basc.framework.util.register.KeyValueRegistry;
import io.basc.framework.util.register.Registration;
import lombok.NonNull;

public class DefaultDelayableNamedEventDispatcher<K, T> extends DefaultNamedEventDispatcher<K, T>
		implements DelayableNamedEventDispatcher<K, T> {
	private final DelayableExecutor delayableExecutor;

	public DefaultDelayableNamedEventDispatcher(
			@NonNull KeyValueRegistry<K, EventDispatcher<T>, ? extends Registration> dispatcherRegistry,
			@NonNull Function<? super K, ? extends EventDispatcher<T>> eventDispatcherFactory,
			@NonNull Matcher<? super K> matcher, @NonNull DelayableExecutor delayableExecutor) {
		super(dispatcherRegistry, eventDispatcherFactory, matcher);
		this.delayableExecutor = delayableExecutor;
	}

	@Override
	public void publishBatchEvents(K name, Elements<T> events) {
		// 不直接publish的目的是为了让消息异步
		publishBatchEvents(name, events, 0, TimeUnit.MILLISECONDS);
	}

	@Override
	public void publishBatchEvents(K name, Elements<T> events, long delay, TimeUnit delayTimeUnit) {
		Assert.requiredArgument(delay >= 0, "delay");
		delayableExecutor.schedule(() -> {
			super.publishBatchEvents(name, events);
		}, delay, delayTimeUnit);
	}

}
