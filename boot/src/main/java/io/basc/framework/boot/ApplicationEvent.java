package io.basc.framework.boot;

import io.basc.framework.util.actor.Event;

public class ApplicationEvent extends Event {
	private static final long serialVersionUID = 1L;

	public ApplicationEvent(Object source) {
		super(source);
	}
}
