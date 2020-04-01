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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.core.Assert;
import scw.lang.NotSupportException;
import scw.mvc.AsyncControl;

/**
 * A {@link ServerHttpAsyncRequestControl} to use on Servlet containers (Servlet
 * 3.0+).
 *
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public class HttpServletAsyncControl implements AsyncControl, AsyncListener {

	private static final long NO_TIMEOUT_VALUE = Long.MIN_VALUE;
	private final MyHttpServletRequest request;
	private final MyHttpServletResponse response;

	private AsyncContext asyncContext;

	private AtomicBoolean asyncCompleted = new AtomicBoolean(false);

	/**
	 * Constructor accepting a request and response pair that are expected to be
	 * of type {@link ServletServerHttpRequest} and
	 * {@link ServletServerHttpResponse} respectively.
	 */
	public HttpServletAsyncControl(MyHttpServletRequest request, MyHttpServletResponse response) {
		Assert.notNull(request, "request is required");
		Assert.notNull(response, "response is required");

		if (!request.getHttpServletRequest().isAsyncSupported()) {
			throw new NotSupportException("Async support must be enabled on a servlet and for all filters involved "
					+ "in async request processing. This is done in Java code using the Servlet API "
					+ "or by adding \"<async-supported>true</async-supported>\" to servlet and "
					+ "filter declarations in web.xml. Also you must use a Servlet 3.0+ container");
		}
		this.request = request;
		this.response = response;
	}

	public boolean isStarted() {
		return (this.asyncContext != null && this.request.getHttpServletRequest().isAsyncStarted());
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

		HttpServletRequest servletRequest = this.request.getHttpServletRequest();
		HttpServletResponse servletResponse = this.response.getHttpServletResponse();
		this.asyncContext = servletRequest.startAsync(servletRequest, servletResponse);
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
		this.asyncContext = null;
		this.asyncCompleted.set(true);
	}

	public void onStartAsync(AsyncEvent event) throws IOException {
	}

	public void onError(AsyncEvent event) throws IOException {
	}

	public void onTimeout(AsyncEvent event) throws IOException {
	}

	public MyHttpServletRequest getRequest() {
		return request;
	}

	public MyHttpServletResponse getResponse() {
		return response;
	}

}
