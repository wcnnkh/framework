package io.basc.framework.event;

import java.util.Arrays;
import java.util.Collection;

public class Events<E extends Event> extends BasicEvent {
	private static final long serialVersionUID = 1L;
	private final Collection<E> events;

	@SafeVarargs
	public Events(E... events) {
		this(Arrays.asList(events));
	}

	public Events(Collection<E> events) {
		this.events = events;
	}

	public Collection<E> getEvents() {
		return events;
	}
}
