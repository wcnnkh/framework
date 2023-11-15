package io.basc.framework.context;

import io.basc.framework.event.ObjectEvent;

public class ApplicationEvent extends ObjectEvent<ApplicationContext> {
	private static final long serialVersionUID = 1L;

	public ApplicationEvent(ApplicationContext source) {
		super(source);
	}

	public ApplicationEvent(ApplicationContext source, long createTime) {
		super(source, createTime);
	}

	public ApplicationEvent(ObjectEvent<ApplicationContext> event) {
		super(event);
	}
}
