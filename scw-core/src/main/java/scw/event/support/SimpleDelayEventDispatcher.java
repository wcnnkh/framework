package scw.event.support;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import scw.event.DelayEventDispatcher;
import scw.event.Event;

public class SimpleDelayEventDispatcher<T extends Event> extends
		SimpleEventDispatcher<T> implements DelayEventDispatcher<T> {
	private final ScheduledExecutorService scheduledExecutorService;

	public SimpleDelayEventDispatcher() {
		this(Executors.newScheduledThreadPool(Runtime.getRuntime()
				.availableProcessors()));
	}

	public SimpleDelayEventDispatcher(
			ScheduledExecutorService scheduledExecutorService) {
		super(true);
		this.scheduledExecutorService = scheduledExecutorService;
	}

	@Override
	public void publishEvent(T event, long delay, TimeUnit delayTimeUnit) {
		if (delay <= 0) {
			publishEvent(event);
			return;
		}

		scheduledExecutorService.schedule(() -> {
			publishEvent(event);
		}, delay, delayTimeUnit);
	}

}
