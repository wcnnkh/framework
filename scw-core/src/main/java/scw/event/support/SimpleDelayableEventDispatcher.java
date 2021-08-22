package scw.event.support;

import java.util.concurrent.TimeUnit;

import scw.core.Assert;
import scw.event.DelayableEventDispatcher;
import scw.event.Event;
import scw.util.concurrent.DefaultDelayableExecutor;
import scw.util.concurrent.DelayableExecutor;

public class SimpleDelayableEventDispatcher<T extends Event> extends SimpleEventDispatcher<T>
		implements DelayableEventDispatcher<T> {
	private final DelayableExecutor delayableExecutor;

	public SimpleDelayableEventDispatcher() {
		this(new DefaultDelayableExecutor());
	}

	public SimpleDelayableEventDispatcher(DelayableExecutor delayableExecutor) {
		super(true);
		this.delayableExecutor = delayableExecutor;
	}

	@Override
	public void publishEvent(T event) {
		//不直接publish的目的是为了让消息异步
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
