package scw.event.support;

import java.util.concurrent.TimeUnit;

import scw.core.Assert;
import scw.event.DelayNamedEventDispatcher;
import scw.event.Event;
import scw.util.concurrent.DefaultDelayableExecutor;
import scw.util.concurrent.DelayableExecutor;

public class SimpleDelayNamedEventDispatcher<K, T extends Event> extends SimpleNamedEventDispatcher<K, T>
		implements DelayNamedEventDispatcher<K, T> {
	private final DelayableExecutor delayExecutor;

	public SimpleDelayNamedEventDispatcher() {
		this(new DefaultDelayableExecutor());
	}

	public SimpleDelayNamedEventDispatcher(DelayableExecutor delayExecutor) {
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
