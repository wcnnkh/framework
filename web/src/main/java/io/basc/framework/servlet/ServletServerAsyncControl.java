package io.basc.framework.servlet;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import io.basc.framework.net.server.ServerAsyncControl;
import io.basc.framework.net.server.ServerAsyncEvent;
import io.basc.framework.net.server.ServerAsyncListener;
import io.basc.framework.util.Assert;

public class ServletServerAsyncControl implements ServerAsyncControl, AsyncListener {
	private static final long NO_TIMEOUT_VALUE = Long.MIN_VALUE;
	private final ServletRequest servletRequest;
	private final ServletResponse servletResponse;
	private final LinkedList<ServerAsyncListener> serverAsyncListeners = new LinkedList<>();
	private AsyncContext asyncContext;
	private final AtomicBoolean asyncCompleted = new AtomicBoolean(false);

	public ServletServerAsyncControl(ServletRequest servletRequest, ServletResponse servletResponse) {
		Assert.notNull(servletRequest, "servletRequest is required");
		Assert.notNull(servletResponse, "servletResponse is required");

		if (!servletRequest.isAsyncSupported()) {
			throw new UnsupportedOperationException(
					"Async support must be enabled on a servlet and for all filters involved "
							+ "in async request processing. This is done in Java code using the Servlet API "
							+ "or by adding \"<async-supported>true</async-supported>\" to servlet and "
							+ "filter declarations in web.xml. Also you must use a Servlet 3.0+ container");
		}
		this.servletRequest = servletRequest;
		this.servletResponse = servletResponse;
	}

	@Override
	public void onComplete(AsyncEvent event) throws IOException {
		if (asyncCompleted.compareAndSet(false, true)) {
			ServerAsyncEvent serverAsyncEvent = new ServletServerAsyncEvent(event, this);
			for (ServerAsyncListener serverAsyncListener : serverAsyncListeners) {
				serverAsyncListener.onComplete(serverAsyncEvent);
			}
			this.asyncContext = null;
		}
	}

	@Override
	public void onTimeout(AsyncEvent event) throws IOException {
		ServerAsyncEvent serverAsyncEvent = new ServletServerAsyncEvent(event, this);
		for (ServerAsyncListener serverAsyncListener : serverAsyncListeners) {
			serverAsyncListener.onTimeout(serverAsyncEvent);
		}
	}

	@Override
	public void onError(AsyncEvent event) throws IOException {
		ServerAsyncEvent serverAsyncEvent = new ServletServerAsyncEvent(event, this);
		for (ServerAsyncListener serverAsyncListener : serverAsyncListeners) {
			serverAsyncListener.onError(serverAsyncEvent);
		}
	}

	@Override
	public void onStartAsync(AsyncEvent event) throws IOException {
		ServerAsyncEvent serverAsyncEvent = new ServletServerAsyncEvent(event, this);
		for (ServerAsyncListener serverAsyncListener : serverAsyncListeners) {
			serverAsyncListener.onStartAsync(serverAsyncEvent);
		}
	}

	@Override
	public void start() {
		start(NO_TIMEOUT_VALUE);
	}

	@Override
	public void start(long timeout) {
		Assert.state(!isCompleted(), "Async processing has already completed");
		if (isStarted()) {
			return;
		}

		this.asyncContext = servletRequest.startAsync(servletRequest, servletResponse);
		this.asyncContext.addListener(this);
		if (timeout != NO_TIMEOUT_VALUE) {
			this.asyncContext.setTimeout(timeout);
		}
	}

	@Override
	public boolean isStarted() {
		return (this.asyncContext != null && servletRequest.isAsyncStarted());
	}

	@Override
	public void complete() {
		if (isStarted() && !isCompleted()) {
			this.asyncContext.complete();
		}
	}

	@Override
	public boolean isCompleted() {
		return this.asyncCompleted.get();
	}

	@Override
	public void addListener(ServerAsyncListener serverHttpAsyncListener) {
		serverAsyncListeners.add(serverHttpAsyncListener);
	}

}
