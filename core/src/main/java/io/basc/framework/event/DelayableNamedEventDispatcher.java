package io.basc.framework.event;

import java.util.concurrent.TimeUnit;

public interface DelayableNamedEventDispatcher<K, T extends Event> extends NamedEventDispatcher<K, T>{
	/**
	 * 延迟触发
	 * @param name
	 * @param event
	 * @param delay
	 * @param delayTimeUnit
	 */
	void publishEvent(K name, T event, long delay, TimeUnit delayTimeUnit);
}
