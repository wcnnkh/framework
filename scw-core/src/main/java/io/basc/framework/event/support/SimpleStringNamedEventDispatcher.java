package io.basc.framework.event.support;

import io.basc.framework.event.Event;
import io.basc.framework.event.EventDispatcher;
import io.basc.framework.util.StringMatcher;
import io.basc.framework.util.StringMatchers;

import java.util.Map.Entry;

public class SimpleStringNamedEventDispatcher<T extends Event> extends SimpleNamedEventDispatcher<String, T> {
	private StringMatcher stringMatcher;

	public SimpleStringNamedEventDispatcher(boolean concurrent) {
		super(concurrent);
		setStringMatcher(StringMatchers.SIMPLE);
	}

	public StringMatcher getStringMatcher() {
		return stringMatcher;
	}

	public void setStringMatcher(StringMatcher stringMatcher) {
		this.stringMatcher = stringMatcher;
	}

	public void publishEvent(String name, T event) {
		if (stringMatcher == null) {
			super.publishEvent(name, event);
		} else {
			for (Entry<String, EventDispatcher<T>> entry : getNamedEventListenerMap().entrySet()) {
				if (stringMatcher.match(entry.getKey(), name)) {
					entry.getValue().publishEvent(event);
				}
			}
		}
	}
}
