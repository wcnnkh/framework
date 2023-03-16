package io.basc.framework.event;

import java.util.concurrent.TimeUnit;

public interface DelayableNamedEventDispatcher<K, T> extends NamedEventDispatcher<K, T> {
	void publishEvent(K name, T event, long delay, TimeUnit delayTimeUnit) throws EventPushException;
}
