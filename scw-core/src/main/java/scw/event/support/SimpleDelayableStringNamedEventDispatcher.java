package scw.event.support;

import java.util.concurrent.TimeUnit;

import scw.core.Assert;
import scw.event.DelayableNamedEventDispatcher;
import scw.event.Event;
import scw.util.concurrent.DefaultDelayableExecutor;
import scw.util.concurrent.DelayableExecutor;

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
