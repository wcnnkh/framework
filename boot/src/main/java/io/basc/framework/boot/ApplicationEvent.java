package io.basc.framework.boot;

import io.basc.framework.event.ObjectEvent;

public class ApplicationEvent extends ObjectEvent<Object> {
	private static final long serialVersionUID = 1L;

	public ApplicationEvent(Object source) {
		super(source);
	}
}
