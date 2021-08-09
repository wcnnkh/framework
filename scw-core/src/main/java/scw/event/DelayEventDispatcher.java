package scw.event;

import java.util.concurrent.TimeUnit;

public interface DelayEventDispatcher<T extends Event> extends EventDispatcher<T>{
	/**
	 * 延迟触发
	 * @param event
	 * @param delay
	 * @param delayTimeUnit
	 */
	void publishEvent(T event, long delay, TimeUnit delayTimeUnit);
}
