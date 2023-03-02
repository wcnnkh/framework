package io.basc.framework.event.support;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import io.basc.framework.event.Event;
import io.basc.framework.event.EventDispatcher;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.NamedEventDispatcher;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Matcher;
import io.basc.framework.util.Registration;

public class SimpleNamedEventDispatcher<K, T extends Event> implements NamedEventDispatcher<K, T> {
	private volatile Map<K, EventDispatcher<T>> namedEventListenerMap;
	@Nullable
	protected final Matcher<K> matcher;

	public SimpleNamedEventDispatcher() {
		this(null);
	}

	public SimpleNamedEventDispatcher(@Nullable Matcher<K> matcher) {
		this.matcher = matcher;
	}

	protected EventDispatcher<T> createEventDispatcher(K name) {
		return new SimpleEventDispatcher<T>();
	}

	public Registration registerListener(K name, EventListener<T> eventListener) {
		synchronized (this) {
			if (namedEventListenerMap == null) {
				namedEventListenerMap = matcher == null ? new HashMap<>(8) : new TreeMap<>(matcher);
			}

			EventDispatcher<T> eventDispatcher = namedEventListenerMap.get(name);
			if (eventDispatcher == null) {
				eventDispatcher = createEventDispatcher(name);
				namedEventListenerMap.put(name, eventDispatcher);
			}
			return eventDispatcher.registerListener(eventListener);
		}
	}

	public void publishEvent(K name, T event) {
		synchronized (this) {
			if (namedEventListenerMap == null) {
				return;
			}

			publishEvent(name, event, namedEventListenerMap);
		}
	}

	protected void publishEvent(K name, T event, Map<K, EventDispatcher<T>> dispatcherMap) {
		if (matcher == null) {
			EventDispatcher<T> dispatcher = dispatcherMap.get(name);
			if (dispatcher == null) {
				return;
			}

			dispatcher.publishEvent(event);
		} else {
			for (Entry<K, EventDispatcher<T>> entry : dispatcherMap.entrySet()) {
				if (matcher.match(entry.getKey(), name) || matcher.match(name, entry.getKey())) {
					entry.getValue().publishEvent(event);
				}
			}
		}
	}
}
