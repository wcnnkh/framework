package scw.event.support;

import java.util.concurrent.TimeUnit;

import scw.core.Assert;
import scw.event.DelayableNamedEventDispatcher;
import scw.event.Event;
import scw.util.concurrent.DefaultDelayableExecutor;
import scw.util.concurrent.DelayableExecutor;

public class SimpleDelayableNamedEventDispatcher<K, T extends Event> extends SimpleNamedEventDispatcher<K, T>
		implements DelayableNamedEventDispatcher<K, T> {
	private final DelayableExecutor delayableExecutor;

	public SimpleDelayableNamedEventDispatcher() {
		this(new DefaultDelayableExecutor());
	}

	public SimpleDelayableNamedEventDispatcher(DelayableExecutor delayableExecutor) {
		super(true);
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
