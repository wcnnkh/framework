package io.basc.framework.event.support;

import java.util.Map;
import java.util.Map.Entry;

import io.basc.framework.event.Event;
import io.basc.framework.event.EventDispatcher;
import io.basc.framework.util.StringMatcher;
import io.basc.framework.util.StringMatchers;

public class SimpleStringNamedEventDispatcher<T extends Event> extends SimpleNamedEventDispatcher<String, T> {
	private final StringMatcher stringMatcher;

	public SimpleStringNamedEventDispatcher() {
		this(StringMatchers.SIMPLE);
	}

	public SimpleStringNamedEventDispatcher(StringMatcher stringMatcher) {
		this.stringMatcher = stringMatcher;
	}

	public StringMatcher getStringMatcher() {
		return stringMatcher;
	}

	@Override
	protected void publishEvent(String name, T event, Map<String, EventDispatcher<T>> dispatcherMap) {
		if (stringMatcher == null) {
			super.publishEvent(name, event, dispatcherMap);
		} else {
			for (Entry<String, EventDispatcher<T>> entry : dispatcherMap.entrySet()) {
				if (stringMatcher.match(entry.getKey(), name)) {
					entry.getValue().publishEvent(event);
				}
			}
		}
	}
}
