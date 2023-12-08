package io.basc.framework.event.support;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import io.basc.framework.event.batch.BatchEventDispatcher;
import io.basc.framework.event.batch.DelayableNamedBatchEventDispatcher;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.concurrent.DelayableExecutor;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.match.Matcher;

public class DefaultDelayableNamedEventDispatcher<K, T> extends DefaultNamedEventDispatcher<K, T>
		implements DelayableNamedBatchEventDispatcher<K, T> {
	private final DelayableExecutor delayableExecutor;

	public DefaultDelayableNamedEventDispatcher(Function<? super K, ? extends BatchEventDispatcher<T>> creator,
			@Nullable Matcher<K> matcher, DelayableExecutor delayableExecutor) {
		super(creator, matcher);
		Assert.requiredArgument(delayableExecutor != null, "delayableExecutor");
		this.delayableExecutor = delayableExecutor;
	}

	@Override
	public void publishBatchEvent(K name, Elements<T> events) {
		// 不直接publish的目的是为了让消息异步
		publishBatchEvent(name, events, 0, TimeUnit.MILLISECONDS);
	}

	@Override
	public void publishBatchEvent(K name, Elements<T> events, long delay, TimeUnit delayTimeUnit) {
		Assert.requiredArgument(delay >= 0, "delay");
		delayableExecutor.schedule(() -> {
			super.publishBatchEvent(name, events);
		}, delay, delayTimeUnit);
	}

}
