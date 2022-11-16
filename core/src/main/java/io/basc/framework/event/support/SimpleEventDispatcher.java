package io.basc.framework.event.support;

import java.util.ArrayList;
import java.util.List;

import io.basc.framework.core.OrderComparator;
import io.basc.framework.core.OrderComparator.OrderSourceProvider;
import io.basc.framework.event.Event;
import io.basc.framework.event.EventDispatcher;
import io.basc.framework.event.EventListener;
import io.basc.framework.lang.AlreadyExistsException;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ConsumeProcessor;
import io.basc.framework.util.Registration;

/**
 * 这是一个同步的事件分发服务
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public class SimpleEventDispatcher<T extends Event> implements EventDispatcher<T> {
	private static final int INITIAL_CAPACITY = Integer.getInteger("io.basc.framework.event.list.initial_capacity", 8);
	private volatile List<EventRegistrationInternal> eventListeners;

	public Registration registerListener(EventListener<T> eventListener) {
		Assert.requiredArgument(eventListener != null, "eventListener");
		EventRegistrationInternal eventRegistration = new EventRegistrationInternal(eventListener);
		synchronized (this) {
			if (eventListeners == null) {
				eventListeners = new ArrayList<>(INITIAL_CAPACITY);
			} else if (eventListeners.contains(eventRegistration)) {
				throw new AlreadyExistsException(eventRegistration.toString());
			}
			eventListeners.add(eventRegistration);
			OrderComparator.sort(eventListeners);
		}
		return eventRegistration;
	}

	public void publishEvent(T event) {
		Assert.requiredArgument(event != null, "event");
		synchronized (this) {
			if (eventListeners == null) {
				return;
			}

			ConsumeProcessor.consumeAll(eventListeners, (e) -> e.getEventListener().onEvent(event));
		}
	}

	private class EventRegistrationInternal implements Registration, OrderSourceProvider {
		private final EventListener<T> eventListener;

		public EventRegistrationInternal(EventListener<T> eventListener) {
			this.eventListener = eventListener;
		}

		public void unregister() {
			synchronized (SimpleEventDispatcher.this) {
				eventListeners.remove(this);
			}
		}

		public EventListener<T> getEventListener() {
			return eventListener;
		}

		@Override
		public Object getOrderSource(Object obj) {
			return eventListener;
		}
	}
}
