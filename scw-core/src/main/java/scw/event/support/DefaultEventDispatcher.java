package scw.event.support;

import java.util.Collection;
import java.util.Iterator;

import scw.core.Assert;
import scw.event.Event;
import scw.event.EventDispatcher;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.lang.AlreadyExistsException;
import scw.util.CollectionFactory;

/**
 * 这是一个同步的事件分发服务
 * @author shuchaowen
 *
 * @param <T>
 */
public class DefaultEventDispatcher<T extends Event> implements EventDispatcher<T> {
	private volatile Collection<EventRegistrationInternal> eventListeners;
	private final boolean concurrent;
	private final int initialCapacity;
	
	public DefaultEventDispatcher(boolean concurrent) {
		this(concurrent, 8);
	}

	public DefaultEventDispatcher(boolean concurrent, int initialCapacity) {
		this.concurrent = concurrent;
		this.initialCapacity = initialCapacity;
	}

	public Collection<EventRegistrationInternal> getEventListeners() {
		if(eventListeners == null){
			synchronized (this) {
				if(eventListeners == null){
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
	 * @param event
	 * @param iterator
	 */
	private void publishEvent(T event, Iterator<EventRegistrationInternal> iterator) {
		if(iterator.hasNext()) {
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
