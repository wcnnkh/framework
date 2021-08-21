package scw.event.support;

import java.util.concurrent.TimeUnit;

import scw.core.Assert;
import scw.event.DelayNamedEventDispatcher;
import scw.event.Event;
import scw.util.concurrent.DefaultDelayExecutor;
import scw.util.concurrent.DelayExecutor;

public class SimpleDelayNamedEventDispatcher<K, T extends Event> extends SimpleNamedEventDispatcher<K, T>
		implements DelayNamedEventDispatcher<K, T> {
	private final DelayExecutor delayExecutor;

	public SimpleDelayNamedEventDispatcher() {
		this(new DefaultDelayExecutor());
	}

	public SimpleDelayNamedEventDispatcher(DelayExecutor delayExecutor) {
		super(true);
		this.delayExecutor = delayExecutor;
	}

	@Override
	public void publishEvent(K name, T event) {
		// 不直接publish的目的是为了让消息异步
		publishEvent(name, event, 0, TimeUnit.MILLISECONDS);
	}

	@Override
	public void publishEvent(K name, T event, long delay, TimeUnit delayTimeUnit) {
		Assert.requiredArgument(delay >= 0, "delay");
		delayExecutor.schedule(() -> {
			super.publishEvent(name, event);
		}, delay, delayTimeUnit);
	}

}
