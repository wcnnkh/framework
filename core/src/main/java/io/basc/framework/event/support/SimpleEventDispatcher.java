package io.basc.framework.event.support;

import java.util.Collection;
import java.util.Iterator;

import io.basc.framework.event.Event;
import io.basc.framework.event.EventDispatcher;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.EventRegistration;
import io.basc.framework.lang.AlreadyExistsException;
import io.basc.framework.util.Assert;
import io.basc.framework.util.CollectionFactory;

/**
 * 这是一个同步的事件分发服务
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public class SimpleEventDispatcher<T extends Event> implements EventDispatcher<T> {
	private volatile Collection<EventRegistrationInternal> eventListeners;
	private final boolean concurrent;
	private final int initialCapacity;

	public SimpleEventDispatcher(boolean concurrent) {
		this(concurrent, 8);
	}

	public SimpleEventDispatcher(boolean concurrent, int initialCapacity) {
		this.concurrent = concurrent;
		this.initialCapacity = initialCapacity;
	}

	public Collection<EventRegistrationInternal> getEventListeners() {
		if (eventListeners == null) {
			synchronized (this) {
				if (eventListeners == null) {
					this.eventListeners = CollectionFactory.createSet(concurrent, initialCapacity);
				}
			}
		}
		return eventListeners;
	}

	public final boolean isConcurrent() {
		return concurrent;
	}

	public EventRegistration registerListener(EventListener<T> eventListener) {
		Assert.requiredArgument(eventListener != null, "eventListener");

		EventRegistration eventRegistration = new EventRegistrationInternal(eventListener);
		if (getEventListeners().contains(eventRegistration)) {
			throw new AlreadyExistsException(eventRegistration.toString());
		}

		getEventListeners().add(new EventRegistrationInternal(eventListener));
		return eventRegistration;
	}

	public void publishEvent(T event) {
		Assert.requiredArgument(event != null, "event");
		publishEvent(event, getEventListeners().iterator());
	}

	/**
	 * 使用此方法的原因是即便发生了异常也将所有的listener通知一遍
	 * 
	 * @param event
	 * @param iterator
	 */
	private void publishEvent(T event, Iterator<EventRegistrationInternal> iterator) {
		if (iterator.hasNext()) {
			EventRegistrationInternal registration = iterator.next();
			try {
				registration.getEventListener().onEvent(event);
			} finally {
				publishEvent(event, iterator);
			}
		}
	}

	private class EventRegistrationInternal implements EventRegistration {
		private final EventListener<T> eventListener;

		public EventRegistrationInternal(EventListener<T> eventListener) {
			this.eventListener = eventListener;
		}

		public void unregister() {
			getEventListeners().remove(this);
		}

		public EventListener<T> getEventListener() {
			return eventListener;
		}
	}
}
