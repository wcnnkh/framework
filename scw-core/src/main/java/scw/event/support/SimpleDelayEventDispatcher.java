package scw.event.support;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import scw.core.Assert;
import scw.event.DelayEventDispatcher;
import scw.event.Event;

public class SimpleDelayEventDispatcher<T extends Event> extends SimpleEventDispatcher<T>
		implements DelayEventDispatcher<T> {
	private final ScheduledExecutorService scheduledExecutorService;

	public SimpleDelayEventDispatcher() {
		this(Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors()));
	}

	public SimpleDelayEventDispatcher(ScheduledExecutorService scheduledExecutorService) {
		super(true);
		this.scheduledExecutorService = scheduledExecutorService;
	}

	@Override
	public void publishEvent(T event) {
		//不直接publish的目的是为了让消息异步
		publishEvent(event, 0, TimeUnit.MILLISECONDS);
	}

	@Override
	public void publishEvent(T event, long delay, TimeUnit delayTimeUnit) {
		Assert.requiredArgument(delay >= 0, "delay");
		scheduledExecutorService.schedule(() -> {
			super.publishEvent(event);
		}, delay, delayTimeUnit);
	}

}
