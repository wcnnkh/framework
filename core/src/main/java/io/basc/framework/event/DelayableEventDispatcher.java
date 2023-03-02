package io.basc.framework.event;

import java.util.concurrent.TimeUnit;

public interface DelayableEventDispatcher<T extends Event> extends EventDispatcher<T> {
	void publishEvent(T event, long delay, TimeUnit delayTimeUnit);
}
