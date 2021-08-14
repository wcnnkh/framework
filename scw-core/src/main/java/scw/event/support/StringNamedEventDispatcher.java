package scw.event.support;

import java.util.Map.Entry;

import scw.event.Event;
import scw.event.EventDispatcher;
import scw.util.StringMatcher;
import scw.util.StringMatchers;

public class StringNamedEventDispatcher<T extends Event> extends SimpleNamedEventDispatcher<String, T> {
	private StringMatcher stringMatcher;

	public StringNamedEventDispatcher(boolean concurrent) {
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
