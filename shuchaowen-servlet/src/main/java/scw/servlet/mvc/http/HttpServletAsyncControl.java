/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package scw.servlet.mvc.http;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;

import scw.core.Assert;
import scw.lang.UnsupportedException;
import scw.mvc.AsyncControl;
import scw.mvc.Channel;
import scw.mvc.MultiAsyncListener;

public class HttpServletAsyncControl implements AsyncControl, AsyncListener {

	private static final long NO_TIMEOUT_VALUE = Long.MIN_VALUE;
	private final HttpServletChannel httpServletChannel;
	private AsyncContext asyncContext;

	private AtomicBoolean asyncCompleted = new AtomicBoolean(false);
	private MultiAsyncListener multiAsyncListener = new MultiAsyncListener();

	public HttpServletAsyncControl(HttpServletChannel httpServletChannel) {
		Assert.notNull(httpServletChannel, "channel is required");

		if (!httpServletChannel.getHttpServletRequest().isAsyncSupported()) {
			throw new UnsupportedException(
					"Async support must be enabled on a servlet and for all filters involved "
							+ "in async request processing. This is done in Java code using the Servlet API "
							+ "or by adding \"<async-supported>true</async-supported>\" to servlet and "
							+ "filter declarations in web.xml. Also you must use a Servlet 3.0+ container");
		}

		this.httpServletChannel = httpServletChannel;
	}

	public boolean isStarted() {
		return (this.asyncContext != null && this.httpServletChannel
				.getHttpServletRequest().isAsyncStarted());
	}

	public boolean isCompleted() {
		return this.asyncCompleted.get();
	}

	public void start() {
		start(NO_TIMEOUT_VALUE);
	}

	public void start(long timeout) {
		Assert.state(!isCompleted(), "Async processing has already completed");
		if (isStarted()) {
			return;
		}

		this.asyncContext = httpServletChannel.getHttpServletRequest()
				.startAsync(httpServletChannel.getHttpServletRequest(),
						httpServletChannel.getHttpServletResponse());
		this.asyncContext.addListener(this);
		if (timeout != NO_TIMEOUT_VALUE) {
			this.asyncContext.setTimeout(timeout);
		}
	}

	public void complete() {
		if (isStarted() && !isCompleted()) {
			this.asyncContext.complete();
		}
	}

	// ---------------------------------------------------------------------
	// Implementation of AsyncListener methods
	// ---------------------------------------------------------------------

	public void onComplete(AsyncEvent event) throws IOException {
		multiAsyncListener.onComplete(new scw.mvc.AsyncEvent(this, event
				.getThrowable()));
		this.asyncContext = null;
		this.asyncCompleted.set(true);
	}

	public void onStartAsync(AsyncEvent event) throws IOException {
		multiAsyncListener.onStartAsync(new scw.mvc.AsyncEvent(this, event
				.getThrowable()));
	}

	public void onError(AsyncEvent event) throws IOException {
		multiAsyncListener.onError(new scw.mvc.AsyncEvent(this, event
				.getThrowable()));
	}

	public void onTimeout(AsyncEvent event) throws IOException {
		multiAsyncListener.onTimeout(new scw.mvc.AsyncEvent(this, event
				.getThrowable()));
	}

	public Channel getChannel() {
		return httpServletChannel;
	}

	public void addListener(scw.mvc.AsyncListener asyncListener) {
		multiAsyncListener.add(asyncListener);
	}

}
