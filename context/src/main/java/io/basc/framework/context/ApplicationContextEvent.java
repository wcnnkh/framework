package io.basc.framework.context;

import io.basc.framework.util.actor.Event;

public class ApplicationContextEvent extends Event {
	private static final long serialVersionUID = 1L;

	public ApplicationContextEvent(ApplicationContext source) {
		super(source);
	}

	@Override
	public ApplicationContext getSource() {
		return (ApplicationContext) super.getSource();
	}
}
