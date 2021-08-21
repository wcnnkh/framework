package scw.event.support;

import java.util.concurrent.TimeUnit;

import scw.core.Assert;
import scw.event.DelayEventDispatcher;
import scw.event.Event;
import scw.util.concurrent.DefaultDelayableExecutor;
import scw.util.concurrent.DelayableExecutor;

public class SimpleDelayEventDispatcher<T extends Event> extends SimpleEventDispatcher<T>
		implements DelayEventDispatcher<T> {
	private final DelayableExecutor delayExecutor;

	public SimpleDelayEventDispatcher() {
		this(new DefaultDelayableExecutor());
	}

	public SimpleDelayEventDispatcher(DelayableExecutor delayExecutor) {
		super(true);
		this.delayExecutor = delayExecutor;
	}

	@Override
	public void publishEvent(T event) {
		//不直接publish的目的是为了让消息异步
		publishEvent(event, 0, TimeUnit.MILLISECONDS);
	}

	@Override
	public void publishEvent(T event, long delay, TimeUnit delayTimeUnit) {
		Assert.requiredArgument(delay >= 0, "delay");
		delayExecutor.schedule(() -> {
			super.publishEvent(event);
		}, delay, delayTimeUnit);
	}

}
