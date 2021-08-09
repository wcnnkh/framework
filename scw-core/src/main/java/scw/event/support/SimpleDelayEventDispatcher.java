package scw.event.support;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import scw.core.Assert;
import scw.event.DelayEventDispatcher;
import scw.event.Event;

public class SimpleDelayEventDispatcher<T extends Event> extends DefaultEventDispatcher<T> implements DelayEventDispatcher<T>{
	private static volatile ScheduledExecutorService executor;
	private static ScheduledExecutorService getScheduledExecutorService(){
		if(executor == null){
			synchronized (SimpleDelayEventDispatcher.class) {
				if(executor == null){
					executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
					Thread shutdown = new Thread(() -> {
						executor.shutdownNow();
					}, "SimpleDelayEventDispatcher-shutdown");
					Runtime.getRuntime().addShutdownHook(shutdown);
				}
			}
		}
		return executor;
	}
	
	private final ScheduledExecutorService scheduledExecutorService;
	private final long delay;
	private final TimeUnit delayTimeUnit;
	
	public SimpleDelayEventDispatcher(){
		this(getScheduledExecutorService());
	}
	
	public SimpleDelayEventDispatcher(ScheduledExecutorService scheduledExecutorService){
		this(scheduledExecutorService, 0, TimeUnit.MILLISECONDS);
	}
	
	public SimpleDelayEventDispatcher(long delay, TimeUnit delayTimeUnit) {
		this(getScheduledExecutorService(), delay, delayTimeUnit);
	}
	
	public SimpleDelayEventDispatcher(ScheduledExecutorService scheduledExecutorService, long delay, TimeUnit delayTimeUnit) {
		super(true);
		Assert.requiredArgument(delay >= 0, "delay");
		this.scheduledExecutorService = scheduledExecutorService;
		this.delay = delay;
		this.delayTimeUnit = delayTimeUnit;
	}

	@Override
	public void publishEvent(T event) {
		if(delay == 0){
			super.publishEvent(event);
		}else{
			publishEvent(event, delay, delayTimeUnit);
		}
	}

	@Override
	public void publishEvent(T event, long delay, TimeUnit delayTimeUnit) {
		scheduledExecutorService.schedule(() -> {
			super.publishEvent(event);
		}, delay, delayTimeUnit);
	}

}
