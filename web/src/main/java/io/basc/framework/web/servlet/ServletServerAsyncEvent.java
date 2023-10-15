package io.basc.framework.web.servlet;

import javax.servlet.AsyncEvent;

import io.basc.framework.web.ServerAsyncEvent;
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
