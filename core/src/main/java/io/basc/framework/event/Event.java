package io.basc.framework.event;

import java.util.EventObject;

public class Event extends EventObject {
	private static final long serialVersionUID = 1L;

	public Event(Object source) {
		super(source);
	}
}