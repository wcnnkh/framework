package io.basc.framework.context;

import io.basc.framework.util.exchange.event.BaseEvent;

public class ApplicationContextEvent extends BaseEvent {
	private static final long serialVersionUID = 1L;

	public ApplicationContextEvent(ApplicationContext source) {
		super(source);
	}

	@Override
	public ApplicationContext getSource() {
		return (ApplicationContext) super.getSource();
	}
}
