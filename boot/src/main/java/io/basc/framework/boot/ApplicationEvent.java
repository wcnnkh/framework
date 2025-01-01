package io.basc.framework.boot;

import io.basc.framework.util.exchange.event.BaseEvent;

public class ApplicationEvent extends BaseEvent {
	private static final long serialVersionUID = 1L;

	public ApplicationEvent(Object source) {
		super(source);
	}
}
