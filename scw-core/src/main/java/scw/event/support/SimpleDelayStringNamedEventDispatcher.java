package scw.event.support;

import java.util.concurrent.TimeUnit;

import scw.core.Assert;
import scw.event.DelayNamedEventDispatcher;
import scw.event.Event;
import scw.util.concurrent.DefaultDelayExecutor;
import scw.util.concurrent.DelayExecutor;

public class SimpleDelayStringNamedEventDispatcher<T extends Event> extends
		SimpleStringNamedEventDispatcher<T> implements
		DelayNamedEventDispatcher<String, T> {
	private final DelayExecutor delayExecutor;

	public SimpleDelayStringNamedEventDispatcher() {
		this(new DefaultDelayExecutor());
	}

	public SimpleDelayStringNamedEventDispatcher(DelayExecutor delayExecutor) {
		super(true);
		this.delayExecutor = delayExecutor;
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
		delayExecutor.schedule(() -> {
			super.publishEvent(name, event);
		}, delay, delayTimeUnit);
	}

}
