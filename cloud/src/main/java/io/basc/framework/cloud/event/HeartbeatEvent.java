package io.basc.framework.cloud.event;

import io.basc.framework.boot.ApplicationEvent;

public class HeartbeatEvent extends ApplicationEvent {
	private static final long serialVersionUID = 1L;
	private final long count;

	public HeartbeatEvent(Object source, long count) {
		super(source);
		this.count = count;
	}

	public long getCount() {
		return count;
	}
}
