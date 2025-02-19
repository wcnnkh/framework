package io.basc.framework.servlet;

import javax.servlet.AsyncEvent;

import io.basc.framework.net.server.ServerAsyncEvent;
import lombok.Data;

@Data
public class ServletServerAsyncEvent implements ServerAsyncEvent {
	private final AsyncEvent asyncEvent;
	private final ServletServerAsyncControl serverAsyncControl;

	@Override
	public Throwable getThrowable() {
		return asyncEvent.getThrowable();
	}
}
