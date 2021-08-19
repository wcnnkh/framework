package scw.event.support;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import scw.core.Assert;
import scw.event.DelayNamedEventDispatcher;
import scw.event.Event;

public class SimpleDelayStringNamedEventDispatcher<T extends Event> extends SimpleStringNamedEventDispatcher<T>
		implements DelayNamedEventDispatcher<String, T> {
	private final ScheduledExecutorService scheduledExecutorService;

	public SimpleDelayStringNamedEventDispatcher() {
		this(Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors()));
	}

	public SimpleDelayStringNamedEventDispatcher(ScheduledExecutorService scheduledExecutorService) {
		super(true);
		this.scheduledExecutorService = scheduledExecutorService;
	}

	@Override
	public void publishEvent(String name, T event) {
		// 不直接publish的目的是为了让消息异步
		publishEvent(name, event, 0, TimeUnit.MILLISECONDS);
	}

	@Override
	public void publishEvent(String name, T event, long delay, TimeUnit delayTimeUnit) {
		Assert.requiredArgument(delay >= 0, "delay");
		scheduledExecutorService.schedule(() -> {
			super.publishEvent(name, event);
		}, delay, delayTimeUnit);
	}

}
